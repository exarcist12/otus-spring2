package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class JdbcGenreRepository implements GenreRepository {

//    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
//
//    public JdbcGenreRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
//        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
//    }


    @PersistenceContext
    private EntityManager em;
    @Override
    public List<Genre> findAll() {
        return em.createQuery("SELECT g FROM Genre g", Genre.class)
                .getResultList();
    }
    @Override
    public List<Genre> findAllByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return em.createQuery("SELECT g FROM Genre g WHERE g.id IN :ids", Genre.class)
                .setParameter("ids", ids)
                .getResultList();
    }
//    @Override
//    public List<Genre> findAll() {
//        return namedParameterJdbcTemplate.query("SELECT id, name FROM genres", new GnreRowMapper());
//    }
//
//    @Override
//    public List<Genre> findAllByIds(Set<Long> ids) {
//        if (ids == null || ids.isEmpty()) {
//            return new ArrayList<>();
//        }
//
//        var sql = "SELECT id, name FROM genres WHERE id IN (:ids)";
//        var params = Map.of("ids", ids);
//
//        return namedParameterJdbcTemplate.query(sql, params, new GnreRowMapper());
//    }

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
