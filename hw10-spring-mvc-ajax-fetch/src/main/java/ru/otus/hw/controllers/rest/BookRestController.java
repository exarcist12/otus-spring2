package ru.otus.hw.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookForm;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.BookService;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;

    @GetMapping
    public List<BookDto> getAllBooks() {
        List<BookDto> books = bookService.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        if (books.isEmpty()) {
            throw new EntityNotFoundException("Books not found");
        }
        return books;
    }

    @GetMapping("/{id}")
    public BookDto getBook(@PathVariable Long id) {
        Book book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        return toDto(book);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDto createBook(@Valid @RequestBody BookForm form) {
        Set<Long> genreIds = form.getGenreIds() != null
                ? new HashSet<>(form.getGenreIds())
                : Collections.emptySet();
        Book book = bookService.insert(form.getTitle(), form.getAuthorId(), genreIds);
        return toDto(book);
    }

    @PutMapping("/{id}")
    public BookDto updateBook(@PathVariable Long id, @Valid @RequestBody BookForm form) {
        Set<Long> genreIds = form.getGenreIds() != null
                ? new HashSet<>(form.getGenreIds())
                : Collections.emptySet();
        Book book = bookService.update(id, form.getTitle(), form.getAuthorId(), genreIds);
        return toDto(book);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    private BookDto toDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor().getId(),
                book.getAuthor().getFullName(),
                book.getGenres().stream()
                        .map(g -> new BookDto.GenreDto(g.getId(), g.getName()))
                        .collect(Collectors.toList())
        );
    }
}