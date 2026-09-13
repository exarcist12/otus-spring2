package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.Set;

@RepositoryRestResource(collectionResourceRel = "genres", path = "genres")
public interface JdbcGenreRepository extends JpaRepository<Genre, Long> {
    @Query("SELECT g FROM Genre g WHERE g.id IN :ids")
    List<Genre> findAllByIds(@Param("ids") Set<Long> ids);

    List<Genre> findAllByIdIn(List<Long> ids);
}