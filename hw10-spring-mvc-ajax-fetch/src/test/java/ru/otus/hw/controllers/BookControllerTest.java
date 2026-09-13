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
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.otus.hw.controllers.rest.BookRestController;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookRestController.class)
@DisplayName("Тесты контроллера книг")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    private List<BookDto> bookDtos;

    @BeforeEach
    void setUp() {
        Author author = new Author(1L, "Author_1");
        List<Genre> genres = List.of(
                new Genre(1L, "Genre_1"),
                new Genre(2L, "Genre_2")
        );
        bookDtos = List.of(
                new BookDto(1L, "Book_1", 1L, "Author_1",
                        genres.stream()
                                .map(g -> new BookDto.GenreDto(g.getId(), g.getName()))
                                .collect(Collectors.toList())),
                new BookDto(2L, "Book_2", 2L, "Author_2",
                        genres.stream()
                                .map(g -> new BookDto.GenreDto(g.getId(), g.getName()))
                                .collect(Collectors.toList()))
        );
    }

    @Test
    @DisplayName("POST /api/books должен создать книгу и вернуть её в JSON")
    void shouldCreateBook() throws Exception {
        BookCreateDto createDto = new BookCreateDto();
        createDto.setTitle("New Book");
        createDto.setAuthorId(1L);
        createDto.setGenreIds(java.util.Set.of(1L, 2L));

        BookDto createdBook = new BookDto(3L, "New Book", 1L, "Author_1",
                List.of(new BookDto.GenreDto(1L, "Genre_1"), new BookDto.GenreDto(2L, "Genre_2")));

        when(bookService.insert(any(BookCreateDto.class))).thenReturn(createdBook);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(3L))
                .andExpect(jsonPath("$.title").value("New Book"));

        verify(bookService, times(1)).insert(any(BookCreateDto.class));
    }

    @Test
    @DisplayName("DELETE /api/books/{id} должен удалить книгу")
    void shouldDeleteBook() throws Exception {
        doNothing().when(bookService).deleteById(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteById(1L);
    }
}