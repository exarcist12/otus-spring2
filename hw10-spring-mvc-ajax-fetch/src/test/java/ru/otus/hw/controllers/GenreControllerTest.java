package ru.otus.hw.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.otus.hw.controllers.rest.GenreRestController;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreRestController.class)
@DisplayName("Тесты контроллера жанров")
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService genreService;

    private List<Genre> genres;

    @BeforeEach
    void setUp() {
        Genre genre1 = new Genre(1L, "Genre_1");
        Genre genre2 = new Genre(2L, "Genre_2");
        genres = List.of(genre1, genre2);
    }

    @Test
    @DisplayName("POST /api/genres должен создать жанр")
    void shouldCreateGenre() throws Exception {
        GenreDto newGenre = new GenreDto(3L, "New Genre");
        when(genreService.insert(any(GenreDto.class))).thenReturn(newGenre);

        GenreDto requestDto = new GenreDto(null, "New Genre");

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(requestDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/genres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.name").value("New Genre"));

        verify(genreService, times(1)).insert(any(GenreDto.class));
    }

    @Test
    @DisplayName("DELETE /api/genres/{id} должен удалить жанр")
    void shouldDeleteGenre() throws Exception {
        doNothing().when(genreService).deleteById(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/genres/1"))
                .andExpect(status().isNoContent());

        verify(genreService, times(1)).deleteById(1L);
    }
}