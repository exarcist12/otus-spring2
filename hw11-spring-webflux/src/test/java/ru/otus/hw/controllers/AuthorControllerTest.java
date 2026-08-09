package ru.otus.hw.controllers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.controllers.rest.AuthorRestController;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(AuthorRestController.class)
@DisplayName("Тесты REST контроллера авторов (реактивный)")
class AuthorControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private AuthorService authorService;

    @Test
    @DisplayName("GET /api/authors возвращает список авторов")
    void shouldReturnAuthors() {
        AuthorDto author1 = new AuthorDto(1L, "Author_1");
        AuthorDto author2 = new AuthorDto(2L, "Author_2");
        when(authorService.findAll()).thenReturn(Flux.just(author1, author2));

        webTestClient.get().uri("/api/authors")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo(1)
                .jsonPath("$[0].fullName").isEqualTo("Author_1")
                .jsonPath("$[1].id").isEqualTo(2)
                .jsonPath("$[1].fullName").isEqualTo("Author_2");
    }

    @Test
    @DisplayName("POST /api/authors создаёт автора и возвращает его")
    void shouldCreateAuthor() {
        AuthorDto requestDto = new AuthorDto(null, "New Author");
        AuthorDto createdDto = new AuthorDto(3L, "New Author");
        when(authorService.insert(any(AuthorDto.class))).thenReturn(Mono.just(createdDto));

        webTestClient.post().uri("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.fullName").isEqualTo("New Author");

        verify(authorService, times(1)).insert(any(AuthorDto.class));
    }

    @Test
    @DisplayName("DELETE /api/authors/{id} удаляет автора")
    void shouldDeleteAuthor() {
        when(authorService.deleteById(1L)).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/authors/1")
                .exchange()
                .expectStatus().isNoContent();

        verify(authorService, times(1)).deleteById(1L);
    }
}