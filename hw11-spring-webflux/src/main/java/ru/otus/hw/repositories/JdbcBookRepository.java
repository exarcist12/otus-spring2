package ru.otus.hw.repositories;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.otus.hw.models.Book;

public interface JdbcBookRepository extends ReactiveCrudRepository<Book, Long> {

    @Query("SELECT b.id AS book_id, b.title AS book_title, " +
            "a.id AS author_id, a.full_name AS author_name, " +
            "g.id AS genre_id, g.name AS genre_name " +
            "FROM books b " +
            "LEFT JOIN authors a ON b.author_id = a.id " +
            "LEFT JOIN books_genres bg ON b.id = bg.book_id " +
            "LEFT JOIN genres g ON bg.genre_id = g.id")
    Flux<BookWithDetails> findAllWithDetails();

    @Query("SELECT b.id AS book_id, b.title AS book_title, " +
            "a.id AS author_id, a.full_name AS author_name, " +
            "g.id AS genre_id, g.name AS genre_name " +
            "FROM books b " +
            "LEFT JOIN authors a ON b.author_id = a.id " +
            "LEFT JOIN books_genres bg ON b.id = bg.book_id " +
            "LEFT JOIN genres g ON bg.genre_id = g.id " +
            "WHERE b.id = :id")
    Flux<BookWithDetails> findByIdWithDetails(Long id);

}