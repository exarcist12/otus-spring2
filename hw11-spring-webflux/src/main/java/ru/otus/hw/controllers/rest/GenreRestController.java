package ru.otus.hw.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreRestController {

    private final GenreService genreService;

    @GetMapping
    public Flux<GenreDto> getAllGenres() {
        return genreService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<GenreDto> createGenre(@RequestBody GenreDto genreDto) {
        return genreService.insert(genreDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteGenre(@PathVariable Long id) {
        return genreService.deleteById(id);
    }
}