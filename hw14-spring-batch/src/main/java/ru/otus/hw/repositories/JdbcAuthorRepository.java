package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.Author;

public interface JdbcAuthorRepository extends JpaRepository<Author, Long> {

    Author findByUserId(Long userId);
}