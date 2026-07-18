package ru.otus.hw.repositories;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Repository
public class JdbcBookRepository implements BookRepository {

    private final JdbcOperations jdbc;

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public JdbcBookRepository(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.jdbc = namedParameterJdbcOperations.getJdbcOperations();
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }



    @Override
    public Optional<Book> findById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        List<Book> books = namedParameterJdbcOperations.query(
                "SELECT b.id, b.title, b.author_id, b.genre_id, " +
                        "a.full_name, g.name " +
                        "FROM books b " +
                        "JOIN authors a ON b.author_id = a.id " +
                        "JOIN genres g ON b.genre_id = g.id " +
                        "WHERE b.id = :id",
                params,
                new BookRowMapper()
        );
        return books.isEmpty() ? Optional.empty() : Optional.of(books.get(0));
    }

    @Override
    public List<Book> findAll() {
        return jdbc.query(
                "SELECT b.id, b.title, b.author_id, b.genre_id, " +
                        "a.full_name, g.name " +
                        "FROM books b " +
                        "JOIN authors a ON b.author_id = a.id " +
                        "JOIN genres g ON b.genre_id = g.id",
                new BookRowMapper()
        );
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
        Map<String, Object> params = Collections.singletonMap("id", id);
        namedParameterJdbcOperations.update("delete from BOOKS where id = :id",
                params);
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());
        params.addValue("genre_id", book.getGenre().getId());
        namedParameterJdbcOperations.update(
                "INSERT INTO books (title, author_id, genre_id) VALUES (:title, :author_id, :genre_id)",
                params,
                keyHolder,
                new String[]{"id"}
        );
        book.setId(keyHolder.getKeyAs(Long.class));
        return book;
    }

    private Book update(Book book) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", book.getId());
        params.addValue("title", book.getTitle());
        params.addValue("author_id", book.getAuthor().getId());
        params.addValue("genre_id", book.getGenre().getId());

        int updatedRows = namedParameterJdbcOperations.update(
                "UPDATE books SET title = :title, author_id = :author_id, genre_id = :genre_id WHERE id = :id",
                params
        );

        if (updatedRows == 0) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(book.getId()));
        }
        return book;
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            long id = rs.getLong("ID");
            String title = rs.getString("TITLE");
            long authorId = rs.getLong("author_id");
            String authorFullName = rs.getString("full_name");
            Author author = new Author(authorId, authorFullName);

            long genreId = rs.getLong("genre_id");
            String genreName = rs.getString("name");
            Genre genre = new Genre(genreId, genreName);

            return new Book(id, title, author, genre);
        }
    }
}
