package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.JdbcAuthorRepository;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.JdbcAuthorRepository;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final JdbcAuthorRepository authorRepository;

    @Override
    public Flux<AuthorDto> findAll() {
        return authorRepository.findAll()
                .map(this::toDto);
    }

    @Override
    public Mono<AuthorDto> insert(AuthorDto authorDto) {
        Author author = new Author();
        author.setFullName(authorDto.getFullName());
        return authorRepository.save(author)
                .map(this::toDto);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return authorRepository.deleteById(id);
    }

    private AuthorDto toDto(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }
}