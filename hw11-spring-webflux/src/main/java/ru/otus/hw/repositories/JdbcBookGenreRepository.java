package ru.otus.hw.repositories;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.BookGenre;

public interface JdbcBookGenreRepository extends ReactiveCrudRepository<BookGenre, Long> {
    Mono<Void> deleteByBookId(Long bookId);
}