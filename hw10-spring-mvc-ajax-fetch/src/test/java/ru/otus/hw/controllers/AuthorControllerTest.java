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
import ru.otus.hw.controllers.rest.AuthorRestController;
import ru.otus.hw.models.Author;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorRestController.class)
@DisplayName("Тесты контроллера авторов")
class AuthorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    private List<Author> authors;

    @BeforeEach
    void setUp() {
        Author author1 = new Author(1L, "Author_1");
        Author author2 = new Author(2L, "Author_2");
        authors = List.of(author1, author2);
    }

    @Test
    @DisplayName("POST /api/authors должен создать автора")
    void shouldCreateAuthor() throws Exception {
        Author newAuthor = new Author(3L, "New Author");
        when(authorService.insert(any(Author.class))).thenReturn(newAuthor);

        String json = "{\"fullName\": \"New Author\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.fullName").value("New Author"));

        verify(authorService, times(1)).insert(any(Author.class));
    }

    @Test
    @DisplayName("DELETE /api/authors/{id} должен удалить автора")
    void shouldDeleteAuthor() throws Exception {
        doNothing().when(authorService).deleteById(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/authors/1"))
                .andExpect(status().isNoContent());

        verify(authorService, times(1)).deleteById(1L);
    }

}