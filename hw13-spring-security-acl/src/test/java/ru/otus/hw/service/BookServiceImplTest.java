package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.repositories.JdbcBookRepository;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class BookServiceImplTest {

    @Autowired
    private BookService bookService;

    @Autowired
    private JdbcBookRepository bookRepository;


    @Test
    @DisplayName("Админ должен видеть ВСЕ книги")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void findAll_shouldReturnAllBooks_forAdmin() {
        List<BookDto> books = bookService.findAll();

        assertThat(books).isNotEmpty();
        assertThat(books.size()).isGreaterThanOrEqualTo(2);
    }



    @Test
    @DisplayName("User1 НЕ может получить чужую книгу по ID (AccessDenied)")
    @WithMockUser(username = "user1", roles = {"USER"})
    void findById_shouldThrowAccessDenied_whenUserIsNotOwner() {

        assertThatThrownBy(() -> bookService.findById(2L))
                .isInstanceOf(AccessDeniedException.class);
    }


    @Test
    @DisplayName("User1 НЕ может создать книгу (доступно только ADMIN)")
    @WithMockUser(username = "user1", roles = {"USER"})
    void insert_shouldThrowAccessDenied_forRegularUser() {
        BookCreateDto createDto = new BookCreateDto();
        createDto.setTitle("Попытка создания");
        createDto.setAuthorId(1L);
        createDto.setGenreIds(Set.of(1L));

        assertThatThrownBy(() -> bookService.insert(createDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("User1 НЕ может обновить даже свою книгу (доступно только ADMIN)")
    @WithMockUser(username = "user1", roles = {"USER"})
    void update_shouldThrowAccessDenied_forRegularUser() {
        BookUpdateDto updateDto = new BookUpdateDto();
        updateDto.setTitle("Попытка изменения");
        updateDto.setAuthorId(1L);
        updateDto.setGenreIds(Set.of(1L));

        assertThatThrownBy(() -> bookService.update(1L, updateDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("User1 НЕ может удалить свою книгу (доступно только ADMIN)")
    @WithMockUser(username = "user1", roles = {"USER"})
    void deleteById_shouldThrowAccessDenied_forRegularUser() {
        assertThatThrownBy(() -> bookService.deleteById(1L))
                .isInstanceOf(AccessDeniedException.class);

        assertThat(bookRepository.findById(1L)).isPresent();
    }


    @Test
    @DisplayName("Админ может создать книгу")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void insert_shouldSucceed_forAdmin() {
        BookCreateDto createDto = new BookCreateDto();
        createDto.setTitle("Книга от Админа");
        createDto.setAuthorId(1L);
        createDto.setGenreIds(Set.of(1L));

        BookDto savedBook = bookService.insert(createDto);

        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Книга от Админа");
    }

    @Test
    @DisplayName("Админ может обновить любую книгу")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void update_shouldSucceed_forAdmin() {
        BookUpdateDto updateDto = new BookUpdateDto();
        updateDto.setTitle("Изменено Админом");
        updateDto.setAuthorId(1L);
        updateDto.setGenreIds(Set.of(1L));

        BookDto updatedBook = bookService.update(1L, updateDto);

        assertThat(updatedBook.getTitle()).isEqualTo("Изменено Админом");
    }

    @Test
    @DisplayName("Админ может удалить любую книгу")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteById_shouldSucceed_forAdmin() {
        assertThat(bookRepository.findById(1L)).isPresent();

        bookService.deleteById(1L);

        assertThat(bookRepository.findById(1L)).isEmpty();
    }
}