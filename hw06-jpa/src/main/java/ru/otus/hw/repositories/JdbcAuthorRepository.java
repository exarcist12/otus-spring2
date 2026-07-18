package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcAuthorRepository implements AuthorRepository {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Author> findAll() {
        return em.createQuery("SELECT a FROM Author a", Author.class)
                .getResultList();
    }

    @Override
    public Optional<Author> findById(long id) {
        return Optional.ofNullable(em.find(Author.class, id));
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong("ID");
            String fullName = rs.getString("FULL_NAME");
            return new Author(id, fullName);
        }
    }
}
