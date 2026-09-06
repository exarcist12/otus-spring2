package ru.otus.hw.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.config.SecurityConfiguration;
import ru.otus.hw.controllers.rest.BookRestController;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.services.BookService;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookRestController.class)
@Import(SecurityConfiguration.class)
class BookRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    void shouldRedirectToLoginWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New\",\"authorId\":1,\"genreIds\":[1]}"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated\",\"authorId\":1,\"genreIds\":[1]}"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldAllowAccessForUser() throws Exception {

        when(bookService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());

        when(bookService.findById(1L)).thenReturn(new BookDto(1L, "Book", 1L, "Author", List.of()));
        mockMvc.perform(get("/api/books/1"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New\",\"authorId\":1,\"genreIds\":[1]}"))
                .andExpect(status().isCreated());

        mockMvc.perform(put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated\",\"authorId\":1,\"genreIds\":[1]}"))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAccessForAdmin() throws Exception {
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNoContent());
    }
}