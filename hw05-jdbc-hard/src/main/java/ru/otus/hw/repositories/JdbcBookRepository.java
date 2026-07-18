package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final GenreRepository genreRepository;


    @Override
    public Optional<Book> findById(long id) {
        var sql = """
            SELECT b.id as book_id, b.title, 
                   a.id as author_id, a.full_name,
                   g.id as genre_id, g.name as genre_name
            FROM books b
            JOIN authors a ON b.author_id = a.id
            LEFT JOIN books_genres bg ON b.id = bg.book_id
            LEFT JOIN genres g ON bg.genre_id = g.id
            WHERE b.id = :id
            """;

        var params = Map.of("id", id);
        var book = namedParameterJdbcTemplate.query(sql, params, new BookResultSetExtractor());
        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var books = getAllBooksWithoutGenres();
        var relations = getAllGenreRelations();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        var deleteRelationsSql = "DELETE FROM books_genres WHERE book_id = :bookId";
        namedParameterJdbcTemplate.update(deleteRelationsSql, Map.of("bookId", id));

        var deleteBookSql = "DELETE FROM books WHERE id = :id";
        var deleted = namedParameterJdbcTemplate.update(deleteBookSql, Map.of("id", id));

        if (deleted == 0) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(id));
        }
    }

    private List<Book> getAllBooksWithoutGenres() {
        var sql = """
            SELECT b.id, b.title, 
                   a.id as author_id, a.full_name
            FROM books b
            JOIN authors a ON b.author_id = a.id
            """;

        return namedParameterJdbcTemplate.query(sql, new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        var sql = "SELECT book_id, genre_id FROM books_genres";
        return namedParameterJdbcTemplate.query(sql, (rs, rowNum) ->
                new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id"))
        );
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {

        var genresMap = genres.stream()
                .collect(Collectors.toMap(Genre::getId, Function.identity()));

        var relationsMap = relations.stream()
                .collect(Collectors.groupingBy(
                        BookGenreRelation::bookId,
                        Collectors.mapping(BookGenreRelation::genreId, Collectors.toList())
                ));

        for (var book : booksWithoutGenres) {
            var genreIds = relationsMap.getOrDefault(book.getId(), List.of());
            var bookGenres = genreIds.stream()
                    .map(genresMap::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            book.setGenres(bookGenres);
        }
    }

    private Book insert(Book book) {
        var sql = "INSERT INTO books (title, author_id) VALUES (:title, :authorId)";
        var keyHolder = new GeneratedKeyHolder();

        var params = Map.of(
                "title", book.getTitle(),
                "authorId", book.getAuthor().getId()
        );

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder);

        book.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private Book update(Book book) {
        var sql = "UPDATE books SET title = :title, author_id = :authorId WHERE id = :id";
        var params = Map.of(
                "id", book.getId(),
                "title", book.getTitle(),
                "authorId", book.getAuthor().getId()
        );

        var updated = namedParameterJdbcTemplate.update(sql, params);

        if (updated == 0) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(book.getId()));
        }

        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        var sql = "INSERT INTO books_genres (book_id, genre_id) VALUES (:bookId, :genreId)";

        var batchParams = book.getGenres().stream()
                .map(genre -> Map.of(
                        "bookId", book.getId(),
                        "genreId", genre.getId()
                ))
                .toArray(Map[]::new);

        namedParameterJdbcTemplate.batchUpdate(sql, batchParams);
    }

    private void removeGenresRelationsFor(Book book) {
        var sql = "DELETE FROM books_genres WHERE book_id = :bookId";
        namedParameterJdbcTemplate.update(sql, Map.of("bookId", book.getId()));
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            var author = new Author();
            author.setId(rs.getLong("author_id"));
            author.setFullName(rs.getString("full_name"));

            var book = new Book();
            book.setId(rs.getLong("id"));
            book.setTitle(rs.getString("title"));
            book.setAuthor(author);
            book.setGenres(new ArrayList<>());

            return book;
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            Book book = null;
            Author author = null;
            Set<Genre> genres = new HashSet<>();

            while (rs.next()) {
                if (book == null) {
                    book = createBook(rs);
                    author = createAuthor(rs);
                    book.setAuthor(author);
                }
                addGenreIfPresent(rs, genres);
            }

            if (book != null) {
                book.setGenres(new ArrayList<>(genres));
            }
            return book;
        }

        private Book createBook(ResultSet rs) throws SQLException {
            Book book = new Book();
            book.setId(rs.getLong("book_id"));
            book.setTitle(rs.getString("title"));
            return book;
        }

        private Author createAuthor(ResultSet rs) throws SQLException {
            Author author = new Author();
            author.setId(rs.getLong("author_id"));
            author.setFullName(rs.getString("full_name"));
            return author;
        }

        private void addGenreIfPresent(ResultSet rs, Set<Genre> genres) throws SQLException {
            long genreId = rs.getLong("genre_id");
            if (!rs.wasNull()) {
                Genre genre = new Genre();
                genre.setId(genreId);
                genre.setName(rs.getString("genre_name"));
                genres.add(genre);
            }
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
