package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.otus.hw.models.Author;

@RepositoryRestResource(collectionResourceRel = "authors", path = "authors")
public interface JdbcAuthorRepository extends JpaRepository<Author, Long> {

    Author findByUserId(Long userId);
}