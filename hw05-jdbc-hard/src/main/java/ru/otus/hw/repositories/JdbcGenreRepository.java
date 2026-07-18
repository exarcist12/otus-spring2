package ru.otus.hw.repositories;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public class JdbcGenreRepository implements GenreRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcGenreRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }


    @Override
    public List<Genre> findAll() {
        return namedParameterJdbcTemplate.query("SELECT id, name FROM genres", new GnreRowMapper());
    }

    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        var sql = "SELECT id, name FROM genres WHERE id IN (:ids)";
        var params = Map.of("ids", ids);

        return namedParameterJdbcTemplate.query(sql, params, new GnreRowMapper());
    }

    private static class GnreRowMapper implements RowMapper<Genre> {

        @Override
        public Genre mapRow(ResultSet rs, int i) throws SQLException {
            var genre = new Genre();
            genre.setId(rs.getLong("id"));
            genre.setName(rs.getString("name"));
            return genre;
        }
    }
}
