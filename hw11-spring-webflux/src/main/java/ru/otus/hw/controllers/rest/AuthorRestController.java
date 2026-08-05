package ru.otus.hw.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorRestController {

    private final AuthorService authorService;

    @GetMapping
    public Flux<AuthorDto> getAllAuthors() {
        return authorService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<AuthorDto> createAuthor(@RequestBody AuthorDto authorDto) {
        return authorService.insert(authorDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAuthor(@PathVariable Long id) {
        return authorService.deleteById(id);
    }
}