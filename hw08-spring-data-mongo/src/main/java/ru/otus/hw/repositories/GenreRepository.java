package ru.otus.hw.repositories;


import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.Genre;

import java.util.List;

public interface GenreRepository extends MongoRepository<Genre, String> {
    // Все методы уже есть в MongoRepository

    // Поиск по списку ID
    List<Genre> findAllByIdIn(List<String> ids);
}
