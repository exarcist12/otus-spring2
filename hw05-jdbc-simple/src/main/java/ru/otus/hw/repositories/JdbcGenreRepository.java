package ru.otus.hw.repositories;

import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class JdbcGenreRepository implements GenreRepository {

    private final JdbcOperations jdbc;
    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public JdbcGenreRepository(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.jdbc = namedParameterJdbcOperations.getJdbcOperations();
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    @Override
    public List<Genre> findAll() {
        return jdbc.query("SELECT id, name FROM genres", new GnreRowMapper());
    }

    @Override
    public Optional<Genre> findById(long id) {
        Map<String, Object> params = Collections.singletonMap("id", id);
        List<Genre> genres = namedParameterJdbcOperations.query(
                "SELECT id, name FROM genres WHERE id = :id",
                params,
                new GnreRowMapper()
        );
        return genres.isEmpty() ? Optional.empty() : Optional.of(genres.get(0));
    }

    private static class GnreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            long id = rs.getLong("id");
            String name = rs.getString("name");
            return new Genre(id, name);
        }
    }
}
