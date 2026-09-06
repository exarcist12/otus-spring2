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
import ru.otus.hw.models.Author;
import ru.otus.hw.models.User;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.CustomUserDetailsService;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    @WithMockUser(roles = "USER", username = "user1")
    void shouldDenyAccessForUser() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUsername("user1");
        user.setRole("ROLE_USER");

        when(userDetailsService.loadUserByUsername("user1")).thenReturn(user);
        when(authorService.findByUserId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/authors"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/authors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"New\"}"))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/authors/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN", username = "admin")
    void shouldAllowAccessForAdmin() throws Exception {
        User admin = new User();
        admin.setId(2L);
        admin.setUsername("admin");
        admin.setRole("ROLE_ADMIN");

        when(userDetailsService.loadUserByUsername("admin")).thenReturn(admin);
        when(authorService.findAll()).thenReturn(List.of());

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
