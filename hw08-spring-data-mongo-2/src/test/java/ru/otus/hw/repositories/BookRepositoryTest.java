package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с книгами (MongoDB)")
@DataMongoTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private GenreRepository genreRepository;

    private List<Author> dbAuthors;
    private List<Genre> dbGenres;
    private List<Book> dbBooks;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();

        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
    }

    @DisplayName("должен загружать книгу по id")
    @Test
    void shouldReturnCorrectBookById() {
        var expectedBook = dbBooks.get(0);

        var actualBook = bookRepository.findById(expectedBook.getId());
        assertThat(actualBook).isPresent()
                .get()
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedBook);
    }

    @DisplayName("должен загружать список всех книг")
    @Test
    void shouldReturnCorrectBooksList() {
        var actualBooks = bookRepository.findAll();
        var expectedBooks = dbBooks;

        assertThat(actualBooks)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expectedBooks);
    }

    @DisplayName("должен сохранять новую книгу")
    @Test
    void shouldSaveNewBook() {
        var expectedBook = new Book(null, "BookTitle_10500", dbAuthors.get(0),
                List.of(dbGenres.get(0), dbGenres.get(2)), List.of());
        var returnedBook = bookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() != null)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedBook);

        assertThat(bookRepository.findById(returnedBook.getId()))
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен сохранять измененную книгу")
    @Test
    void shouldSaveUpdatedBook() {
        var newBook = new Book(null, "BookTitle_Original", dbAuthors.get(0),
                List.of(dbGenres.get(0), dbGenres.get(1)), List.of());
        var savedBook = bookRepository.save(newBook);

        var bookId = savedBook.getId();
        var expectedBook = new Book(bookId, "BookTitle_10500", dbAuthors.get(2),
                List.of(dbGenres.get(4), dbGenres.get(5)), List.of());

        assertThat(bookRepository.findById(bookId))
                .isPresent()
                .get()
                .isNotEqualTo(expectedBook);

        var returnedBook = bookRepository.save(expectedBook);
        assertThat(returnedBook).isNotNull()
                .matches(book -> book.getId() != null)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .isEqualTo(expectedBook);

        assertThat(bookRepository.findById(bookId))
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(returnedBook);
    }

    @DisplayName("должен удалять книгу по id")
    @Test
    void shouldDeleteBook() {
        var bookId = dbBooks.get(0).getId();

        assertThat(bookRepository.findById(bookId)).isPresent();
        bookRepository.deleteById(bookId);
        assertThat(bookRepository.findById(bookId)).isEmpty();
    }

    private List<Author> getDbAuthors() {
        return List.of(
                        new Author(null, "Author_1"),
                        new Author(null, "Author_2"),
                        new Author(null, "Author_3")
                ).stream()
                .map(authorRepository::save)
                .toList();
    }

    private List<Genre> getDbGenres() {
        return List.of(
                        new Genre(null, "Genre_1"),
                        new Genre(null, "Genre_2"),
                        new Genre(null, "Genre_3"),
                        new Genre(null, "Genre_4"),
                        new Genre(null, "Genre_5"),
                        new Genre(null, "Genre_6")
                ).stream()
                .map(genreRepository::save)
                .toList();
    }

    private List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return List.of(
                        new Book(null, "BookTitle_1", dbAuthors.get(0),
                                dbGenres.subList(0, 2), List.of()),
                        new Book(null, "BookTitle_2", dbAuthors.get(1),
                                dbGenres.subList(2, 4), List.of()),
                        new Book(null, "BookTitle_3", dbAuthors.get(2),
                                dbGenres.subList(4, 6), List.of())
                ).stream()
                .map(bookRepository::save)
                .toList();
    }
}