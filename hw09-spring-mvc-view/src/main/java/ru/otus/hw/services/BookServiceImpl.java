package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.JdbcAuthorRepository;
import ru.otus.hw.repositories.JdbcBookRepository;
import ru.otus.hw.repositories.JdbcGenreRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional
public class BookServiceImpl implements BookService {

    private final JdbcAuthorRepository authorRepository;

    private final JdbcGenreRepository genreRepository;

    private final JdbcBookRepository bookRepository;

    @Override
    @Transactional(readOnly = true)
    public BookDto findById(long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
        return toDto(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public BookDto insert(BookCreateDto createDto) {
        Author author = authorRepository.findById(createDto.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));

        List<Genre> genres = genreRepository.findAllByIds(createDto.getGenreIds());
        if (genres.size() != createDto.getGenreIds().size()) {
            throw new EntityNotFoundException("Some genres not found");
        }

        Book book = new Book();
        book.setTitle(createDto.getTitle());
        book.setAuthor(author);
        book.setGenres(genres);

        Book saved = bookRepository.save(book);
        return toDto(saved);
    }

    @Override
    public BookDto update(long id, BookUpdateDto updateDto) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        Author author = authorRepository.findById(updateDto.getAuthorId())
                .orElseThrow(() -> new EntityNotFoundException("Author not found"));

        List<Genre> newGenres = genreRepository.findAllByIds(updateDto.getGenreIds());
        if (newGenres.size() != updateDto.getGenreIds().size()) {
            throw new EntityNotFoundException("Some genres not found");
        }

        book.setTitle(updateDto.getTitle());
        book.setAuthor(author);
        book.getGenres().clear();
        book.getGenres().addAll(newGenres);

        Book updated = bookRepository.save(book);
        return toDto(updated);
    }

    @Override
    public void deleteById(long id) {
        bookRepository.deleteById(id);
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