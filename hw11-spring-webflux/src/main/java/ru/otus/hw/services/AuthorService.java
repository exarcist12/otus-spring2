package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;

import java.util.List;

public interface AuthorService {

    Flux<AuthorDto> findAll();

    Mono<AuthorDto> insert(AuthorDto authorDto);

    Mono<Void> deleteById(Long id);
}