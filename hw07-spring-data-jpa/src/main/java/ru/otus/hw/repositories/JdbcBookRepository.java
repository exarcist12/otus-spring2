package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

public interface JdbcBookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(attributePaths = {"author", "genres"})
    @Query("SELECT b FROM Book b WHERE b.id = :id")
    Optional<Book> findById(long id);


    @EntityGraph(attributePaths = {"author", "genres"})
    @Query("SELECT b FROM Book b")
    List<Book> findAllWithGenres();

    @Override
    @EntityGraph(attributePaths = {"author", "genres"})
    List<Book> findAll();

    @Override
    @EntityGraph(attributePaths = {"author", "genres"})
    Optional<Book> findById(Long id);
}
