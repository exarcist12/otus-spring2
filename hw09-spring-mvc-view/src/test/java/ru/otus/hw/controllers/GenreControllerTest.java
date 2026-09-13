package ru.otus.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GenreController.class)
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
    @DisplayName("GET /genres должен возвращать страницу со списком жанров")
    void shouldReturnGenresList() throws Exception {
        when(genreService.findAll()).thenReturn(genres);

        mockMvc.perform(MockMvcRequestBuilders.get("/genres"))
                .andExpect(status().isOk())
                .andExpect(view().name("genres"))
                .andExpect(model().attribute("genres", hasSize(2)))
                .andExpect(model().attribute("genres", containsInAnyOrder(
                        hasProperty("id", is(1L)),
                        hasProperty("id", is(2L))
                )))
                .andExpect(model().attributeExists("genre"))
                .andExpect(model().attribute("genre", hasProperty("id", is(0L))))
                .andExpect(model().attribute("genre", hasProperty("name", nullValue())));

        verify(genreService, times(1)).findAll();
    }

    @Test
    @DisplayName("POST /genres должен создать жанр")
    void shouldCreateGenreAndRedirect() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", "New Genre");

        when(genreService.insert(any(Genre.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(MockMvcRequestBuilders.post("/genres")
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/genres"));

        verify(genreService, times(1)).insert(any(Genre.class));
        verify(genreService).insert(argThat(genre -> genre.getName().equals("New Genre")));
    }

    @Test
    @DisplayName("POST /genres/{id}/delete должен удалить жанр")
    void shouldDeleteGenreAndRedirect() throws Exception {
        doNothing().when(genreService).deleteById(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/genres/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/genres"));

        verify(genreService, times(1)).deleteById(1L);
    }
}