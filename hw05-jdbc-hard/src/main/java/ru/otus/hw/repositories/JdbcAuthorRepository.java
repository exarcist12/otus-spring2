package ru.otus.hw.repositories;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcAuthorRepository implements AuthorRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcAuthorRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Author> findAll() {
        return namedParameterJdbcTemplate.query("select ID, FULL_NAME from AUTHORS", new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(long id) {

        var sql = "SELECT id, full_name FROM authors WHERE id = :id";
        var params = Map.of("id", id);

        var authors = namedParameterJdbcTemplate.query(sql, params, new AuthorRowMapper());
        return authors.stream().findFirst();
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
