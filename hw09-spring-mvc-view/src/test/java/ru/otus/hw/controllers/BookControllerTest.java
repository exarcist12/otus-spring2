package ru.otus.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@DisplayName("Тесты контроллера книг")
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private CommentService commentService;

    private List<Author> authors;
    private List<Genre> genres;
    private List<BookDto> bookDtos;

    @BeforeEach
    void setUp() {
        authors = List.of(
                new Author(1L, "Author_1"),
                new Author(2L, "Author_2")
        );
        genres = List.of(
                new Genre(1L, "Genre_1"),
                new Genre(2L, "Genre_2")
        );

        // Создаём DTO для книг
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
    @DisplayName("GET /books должен возвращать страницу со списком книг")
    void shouldReturnBooksList() throws Exception {
        when(bookService.findAll()).thenReturn(bookDtos);
        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);

        mockMvc.perform(MockMvcRequestBuilders.get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("books", hasSize(2)))
                .andExpect(model().attribute("authors", authors))
                .andExpect(model().attribute("genres", genres))
                .andExpect(model().attributeExists("bookCreateDto"));

        verify(bookService, times(1)).findAll();
        verify(authorService, times(1)).findAll();
        verify(genreService, times(1)).findAll();
    }

    @Test
    @DisplayName("POST /books с валидными данными должен создать книгу и перенаправить на главную")
    void shouldCreateBookAndRedirect() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "New Book");
        params.add("authorId", "1");
        params.add("genreIds", "1");
        params.add("genreIds", "2");

        BookDto createdBook = new BookDto(3L, "New Book", 1L, "Author_1",
                genres.stream()
                        .map(g -> new BookDto.GenreDto(g.getId(), g.getName()))
                        .collect(Collectors.toList()));

        // Мокаем insert с BookCreateDto
        when(bookService.insert(any(BookCreateDto.class))).thenReturn(createdBook);

        mockMvc.perform(MockMvcRequestBuilders.post("/books")
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService, times(1)).insert(any(BookCreateDto.class));
    }

    @Test
    @DisplayName("POST /books с ошибками валидации должен вернуть ту же страницу с ошибками")
    void shouldReturnFormWithErrorsOnInvalidData() throws Exception {
        when(bookService.findAll()).thenReturn(bookDtos);
        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "");
        params.add("authorId", "1");
        params.add("genreIds", "1");

        mockMvc.perform(MockMvcRequestBuilders.post("/books")
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeHasFieldErrors("bookCreateDto", "title"))
                .andExpect(model().attribute("books", bookDtos));

        verify(bookService, never()).insert(any(BookCreateDto.class));
    }

    @Test
    @DisplayName("GET /books/{id} должен возвращать страницу с деталями книги и комментариями")
    void shouldReturnBookDetailsPage() throws Exception {
        BookDto book = bookDtos.get(0);
        List<Comment> comments = List.of(
                new Comment(1L, "Great!", null),
                new Comment(2L, "Nice", null)
        );

        when(bookService.findById(1L)).thenReturn(book);
        when(commentService.findByBookId(1L)).thenReturn(comments);

        mockMvc.perform(MockMvcRequestBuilders.get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("book-details"))
                .andExpect(model().attribute("book", book))
                .andExpect(model().attribute("comments", comments));

        verify(bookService, times(1)).findById(1L);
        verify(commentService, times(1)).findByBookId(1L);
    }

    @Test
    @DisplayName("POST /books/{id} должен обновить книгу и перенаправить на главную")
    void shouldUpdateBookAndRedirect() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "Updated Title");
        params.add("authorId", "2");
        params.add("genreIds", "2");

        mockMvc.perform(MockMvcRequestBuilders.post("/books/1")
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService, times(1)).update(eq(1L), any(BookUpdateDto.class));
    }

    @Test
    @DisplayName("POST /books/{id}/delete должен удалить книгу и перенаправить на главную")
    void shouldDeleteBookAndRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/books/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService, times(1)).deleteById(1L);
    }
}