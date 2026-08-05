package ru.otus.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.controllers.rest.BookRestController;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.services.BookService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(BookRestController.class)
@DisplayName("Тесты REST контроллера книг (реактивный)")
class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BookService bookService;

    private List<BookDto> bookDtos;

    @BeforeEach
    void setUp() {
        bookDtos = List.of(
                new BookDto(1L, "Book 1", 1L, "Author 1", List.of(new BookDto.GenreDto(1L, "Genre 1"))),
                new BookDto(2L, "Book 2", 2L, "Author 2", List.of())
        );
    }

    @Test
    @DisplayName("GET /api/books возвращает список книг")
    void shouldReturnBooksList() {
        when(bookService.findAll()).thenReturn(Flux.fromIterable(bookDtos));

        webTestClient.get()
                .uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].title").isEqualTo("Book 1");
    }

    @Test
    @DisplayName("GET /api/books/{id} возвращает книгу по ID")
    void shouldReturnBookById() {
        BookDto book = bookDtos.get(0);
        when(bookService.findById(1L)).thenReturn(Mono.just(book));

        webTestClient.get()
                .uri("/api/books/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.title").isEqualTo("Book 1");
    }

    @Test
    @DisplayName("POST /api/books создаёт книгу")
    void shouldCreateBook() {
        BookCreateDto createDto = new BookCreateDto();
        createDto.setTitle("New Book");
        createDto.setAuthorId(1L);
        createDto.setGenreIds(java.util.Set.of(1L));

        BookDto created = new BookDto(3L, "New Book", 1L, "Author 1", List.of(new BookDto.GenreDto(1L, "Genre 1")));
        when(bookService.insert(any(BookCreateDto.class))).thenReturn(Mono.just(created));

        webTestClient.post()
                .uri("/api/books")
                .bodyValue(createDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.title").isEqualTo("New Book");
    }

    @Test
    @DisplayName("PUT /api/books/{id} обновляет книгу")
    void shouldUpdateBook() {
        BookUpdateDto updateDto = new BookUpdateDto();
        updateDto.setTitle("Updated Book");
        updateDto.setAuthorId(1L);
        updateDto.setGenreIds(java.util.Set.of(2L));

        BookDto updated = new BookDto(1L, "Updated Book", 1L, "Author 1", List.of(new BookDto.GenreDto(2L, "Genre 2")));
        when(bookService.update(eq(1L), any(BookUpdateDto.class))).thenReturn(Mono.just(updated));

        webTestClient.put()
                .uri("/api/books/1")
                .bodyValue(updateDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Updated Book");
    }

    @Test
    @DisplayName("DELETE /api/books/{id} удаляет книгу")
    void shouldDeleteBook() {
        when(bookService.deleteById(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/books/1")
                .exchange()
                .expectStatus().isNoContent();
    }
}