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
import ru.otus.hw.models.Author;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthorController.class)
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
    @DisplayName("GET /authors должен возвращать страницу со списком авторов")
    void shouldReturnAuthorsList() throws Exception {
        when(authorService.findAll()).thenReturn(authors);

        mockMvc.perform(MockMvcRequestBuilders.get("/authors"))
                .andExpect(status().isOk())
                .andExpect(view().name("authors"))
                .andExpect(model().attribute("authors", hasSize(2)))
                .andExpect(model().attribute("authors", containsInAnyOrder(
                        hasProperty("id", is(1L)),
                        hasProperty("id", is(2L))
                )))
                .andExpect(model().attributeExists("author"))
                .andExpect(model().attribute("author", hasProperty("id", is(0L))))
                .andExpect(model().attribute("author", hasProperty("fullName", nullValue())));

        verify(authorService, times(1)).findAll();
    }

    @Test
    @DisplayName("POST /authors должен создать автора")
    void shouldCreateAuthorAndRedirect() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("fullName", "New Author");

        when(authorService.insert(any(Author.class))).thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(MockMvcRequestBuilders.post("/authors")
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/authors"));

        verify(authorService, times(1)).insert(any(Author.class));
        verify(authorService).insert(argThat(author -> author.getFullName().equals("New Author")));
    }

    @Test
    @DisplayName("POST /authors/{id}/delete должен удалить автора")
    void shouldDeleteAuthorAndRedirect() throws Exception {
        doNothing().when(authorService).deleteById(1L);

        mockMvc.perform(MockMvcRequestBuilders.post("/authors/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/authors"));

        verify(authorService, times(1)).deleteById(1L);
    }

}