package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.controllers.rest.GenreRestController;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(GenreRestController.class)
@DisplayName("Тесты REST контроллера жанров (реактивный)")
class GenreControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private GenreService genreService;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    @DisplayName("GET /api/genres возвращает список жанров")
    void shouldReturnGenresList() {
        List<GenreDto> genres = List.of(
                new GenreDto(1L, "Genre_1"),
                new GenreDto(2L, "Genre_2")
        );
        when(genreService.findAll()).thenReturn(Flux.fromIterable(genres));

        webTestClient.get()
                .uri("/api/genres")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray()
                .jsonPath("$.length()").isEqualTo(2)
                .jsonPath("$[0].name").isEqualTo("Genre_1");
    }

    @Test
    @DisplayName("POST /api/genres должен создать жанр")
    void shouldCreateGenre() throws Exception {
        GenreDto requestDto = new GenreDto(null, "New Genre");
        GenreDto responseDto = new GenreDto(3L, "New Genre");

        when(genreService.insert(any(GenreDto.class))).thenReturn(Mono.just(responseDto));

        String json = mapper.writeValueAsString(requestDto);

        webTestClient.post()
                .uri("/api/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isEqualTo(3)
                .jsonPath("$.name").isEqualTo("New Genre");

        verify(genreService, times(1)).insert(any(GenreDto.class));
    }

    @Test
    @DisplayName("DELETE /api/genres/{id} должен удалить жанр")
    void shouldDeleteGenre() {
        when(genreService.deleteById(1L)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/genres/1")
                .exchange()
                .expectStatus().isNoContent();

        verify(genreService, times(1)).deleteById(1L);
    }
}