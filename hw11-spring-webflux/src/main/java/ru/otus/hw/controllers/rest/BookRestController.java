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
import org.springframework.web.bind.annotation.PutMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.services.BookService;

import jakarta.validation.Valid;

import java.time.Duration;

import static org.springframework.shell.command.invocation.InvocableShellMethod.log;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;

    @GetMapping
    public Flux<BookDto> getAllBooks() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<BookDto> getBook(@PathVariable Long id) {
        return bookService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookDto> createBook(@Valid @RequestBody BookCreateDto createDto) {
        return bookService.insert(createDto);
    }

    @PutMapping("/{id}")
    public Mono<BookDto> updateBook(@PathVariable Long id, @Valid @RequestBody BookUpdateDto updateDto) {
        return bookService.update(id, updateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBook(@PathVariable Long id) {
        return bookService.deleteById(id);
    }

    @GetMapping("/delay/{seconds}")
    public Mono<String> delay(@PathVariable int seconds) {
        log.info("Запрос на задержку {} секунд, поток: {}", seconds, Thread.currentThread().getName());
        return Mono.delay(Duration.ofSeconds(seconds))
                .doOnNext(l -> log.info("Ответ через {} секунд, поток: {}", seconds, Thread.currentThread().getName()))
                .map(l -> "Ответ через " + seconds + " секунд");
    }
}