package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {


    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Book> findById(long id) {
        try {
            var book = em.createQuery(
                            "SELECT b FROM Book b " +
                                    "JOIN FETCH b.author " +
                                    "LEFT JOIN FETCH b.genres " +
                                    "WHERE b.id = :id", Book.class)
                    .setParameter("id", id)
                    .getSingleResult();
            return Optional.of(book);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Book> findAll() {
        return em.createQuery(
                        "SELECT DISTINCT b FROM Book b " +
                                "JOIN FETCH b.author " +
                                "LEFT JOIN FETCH b.genres", Book.class)
                .getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        } else {
            return em.merge(book);
        }
    }

    @Override
    public void deleteById(long id) {
        em.createQuery("DELETE FROM Book b WHERE b.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

}
