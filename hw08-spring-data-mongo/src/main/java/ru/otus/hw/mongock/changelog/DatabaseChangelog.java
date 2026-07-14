package ru.otus.hw.mongock.changelog;  // ← ПРОВЕРЬТЕ ЭТОТ ПАКЕТ!

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.GenreRepository;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;

@ChangeLog
public class DatabaseChangelog {

    @ChangeSet(order = "001", id = "dropDb", author = "student", runAlways = true)
    public void dropDb(MongoDatabase db) {
        db.drop();
    }

    @ChangeSet(order = "002", id = "insertAuthors", author = "student")
    public void insertAuthors(AuthorRepository authorRepository) {
        authorRepository.saveAll(List.of(
                new Author(null, "Author_1"),
                new Author(null, "Author_2"),
                new Author(null, "Author_3")
        ));
    }

    @ChangeSet(order = "003", id = "insertGenres", author = "student")
    public void insertGenres(GenreRepository genreRepository) {
        genreRepository.saveAll(List.of(
                new Genre(null, "Genre_1"),
                new Genre(null, "Genre_2"),
                new Genre(null, "Genre_3"),
                new Genre(null, "Genre_4"),
                new Genre(null, "Genre_5"),
                new Genre(null, "Genre_6")
        ));
    }

    @ChangeSet(order = "004", id = "insertBooks", author = "student")
    public void insertBooks(AuthorRepository authorRepository,
                            GenreRepository genreRepository,
                            BookRepository bookRepository) {
        var authors = authorRepository.findAll();
        var genres = genreRepository.findAll();

        if (authors.size() >= 3 && genres.size() >= 6) {
            bookRepository.saveAll(List.of(
                    new Book(null, "BookTitle_1", authors.get(0),
                            List.of(genres.get(0), genres.get(1)), List.of()),
                    new Book(null, "BookTitle_2", authors.get(1),
                            List.of(genres.get(2), genres.get(3)), List.of()),
                    new Book(null, "BookTitle_3", authors.get(2),
                            List.of(genres.get(4), genres.get(5)), List.of())
            ));
        }
    }
}