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
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
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
    private List<Book> books;

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
        books = List.of(
                new Book(1L, "Book_1", authors.get(0), genres),
                new Book(2L, "Book_2", authors.get(1), genres)
        );
    }

    @Test
    @DisplayName("GET /books должен возвращать страницу со списком книг")
    void shouldReturnBooksList() throws Exception {
        when(bookService.findAll()).thenReturn(books);
        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);

        mockMvc.perform(MockMvcRequestBuilders.get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("books", hasSize(2)))
                .andExpect(model().attribute("authors", authors))
                .andExpect(model().attribute("genres", genres))
                .andExpect(model().attributeExists("bookForm"));

        verify(bookService, times(1)).findAll();
        verify(authorService, times(1)).findAll();
        verify(genreService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET / должен возвращать ту же страницу, что и /books")
    void shouldReturnSamePageForRoot() throws Exception {
        when(bookService.findAll()).thenReturn(books);
        when(authorService.findAll()).thenReturn(authors);
        when(genreService.findAll()).thenReturn(genres);

        mockMvc.perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attribute("books", hasSize(2)));
    }

    @Test
    @DisplayName("POST /books с валидными данными должен создать книгу и перенаправить на главную")
    void shouldCreateBookAndRedirect() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("title", "New Book");
        params.add("authorId", "1");
        params.add("genreIds", "1");
        params.add("genreIds", "2");

        when(bookService.insert(anyString(), anyLong(), anySet())).thenReturn(new Book(3L, "New Book", authors.get(0), genres));

        mockMvc.perform(MockMvcRequestBuilders.post("/books")
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService, times(1)).insert(eq("New Book"), eq(1L), eq(Set.of(1L, 2L)));
    }

    @Test
    @DisplayName("POST /books с ошибками валидации должен вернуть ту же страницу с ошибками")
    void shouldReturnFormWithErrorsOnInvalidData() throws Exception {
        when(bookService.findAll()).thenReturn(books);
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
                .andExpect(model().attributeHasFieldErrors("bookForm", "title"))
                .andExpect(model().attribute("books", books));

        verify(bookService, never()).insert(anyString(), anyLong(), anySet());
    }

    @Test
    @DisplayName("GET /books/{id} должен возвращать страницу с деталями книги и комментариями")
    void shouldReturnBookDetailsPage() throws Exception {
        Book book = books.get(0);
        List<Comment> comments = List.of(
                new Comment(1L, "Great!", book),
                new Comment(2L, "Nice", book)
        );

        when(bookService.findById(1L)).thenReturn(Optional.of(book));
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
                .andExpect(redirectedUrl("/"));

        verify(bookService, times(1)).update(eq(1L), eq("Updated Title"), eq(2L), eq(Set.of(2L)));
    }

    @Test
    @DisplayName("POST /books/{id}/delete должен удалить книгу и перенаправить на главную")
    void shouldDeleteBookAndRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/books/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));

        verify(bookService, times(1)).deleteById(1L);
    }
}