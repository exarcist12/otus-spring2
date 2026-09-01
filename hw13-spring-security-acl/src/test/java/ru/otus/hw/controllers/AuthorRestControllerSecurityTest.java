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
import ru.otus.hw.controllers.rest.AuthorRestController;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthorRestController.class)
@Import(SecurityConfiguration.class)
class AuthorRestControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthorService authorService;

    @Test
    void shouldRedirectToLoginWhenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"New\"}"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().is3xxRedirection());
        mockMvc.perform(get("/api/authors/current"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldDenyAccessForUser() throws Exception {
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"New\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldAllowAccessForAdmin() throws Exception {
        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"New\"}"))
                .andExpect(status().isCreated());
        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isNoContent());
    }
}
