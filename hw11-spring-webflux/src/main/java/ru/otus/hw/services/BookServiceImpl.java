package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookCreateDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.BookUpdateDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.BookGenre;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.JdbcAuthorRepository;
import ru.otus.hw.repositories.JdbcBookGenreRepository;
import ru.otus.hw.repositories.JdbcBookRepository;
import ru.otus.hw.repositories.JdbcGenreRepository;
import ru.otus.hw.repositories.BookWithDetails;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final JdbcBookRepository jdbcBookRepository;

    private final JdbcAuthorRepository jdbcAuthorRepository;

    private final JdbcGenreRepository jdbcGenreRepository;

    private final JdbcBookGenreRepository jdbcBookGenreRepository;

    @Override
    public Flux<BookDto> findAll() {
        return jdbcBookRepository.findAllWithDetails()
                .collectList()
                .flatMapMany(rows -> {
                    Map<Long, BookDto> bookMap = new LinkedHashMap<>();
                    rows.forEach(row -> {
                        BookDto book = bookMap.computeIfAbsent(row.getBookId(),
                                id -> new BookDto(id, row.getBookTitle(), row.getAuthorId(), row.getAuthorName(), new ArrayList<>()));
                        if (row.getGenreId() != null) {
                            book.getGenres().add(new BookDto.GenreDto(row.getGenreId(), row.getGenreName()));
                        }
                    });
                    return Flux.fromIterable(bookMap.values());
                });
    }

    @Override
    public Mono<BookDto> findById(Long id) {
        return jdbcBookRepository.findByIdWithDetails(id)
                .collectList()
                .flatMap(rows -> {
                    if (rows.isEmpty()) {
                        return Mono.error(new EntityNotFoundException("Book not found"));
                    }
                    BookDto book = null;
                    for (BookWithDetails row : rows) {
                        if (book == null) {
                            book = new BookDto(row.getBookId(), row.getBookTitle(), row.getAuthorId(), row.getAuthorName(), new ArrayList<>());
                        }
                        if (row.getGenreId() != null) {
                            book.getGenres().add(new BookDto.GenreDto(row.getGenreId(), row.getGenreName()));
                        }
                    }
                    return Mono.just(book);
                });
    }

    @Override
    public Mono<BookDto> insert(BookCreateDto createDto) {
        return jdbcAuthorRepository.findById(createDto.getAuthorId())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Author not found")))
                .zipWith(jdbcGenreRepository.findAllByIdIn(createDto.getGenreIds().stream().toList())
                        .collectList()
                        .filter(list -> list.size() == createDto.getGenreIds().size())
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Some genres not found")))
                )
                .flatMap(tuple -> {
                    Author author = tuple.getT1();
                    List<Genre> genres = tuple.getT2();
                    Book book = new Book();
                    book.setTitle(createDto.getTitle());
                    book.setAuthorId(author.getId());
                    return jdbcBookRepository.save(book)
                            .flatMap(savedBook -> {
                                List<BookGenre> relations = genres.stream()
                                        .map(g -> new BookGenre(savedBook.getId(), g.getId()))
                                        .toList();
                                return Flux.fromIterable(relations)
                                        .flatMap(jdbcBookGenreRepository::save)
                                        .then(Mono.just(savedBook))
                                        .map(saved -> {
                                            List<BookDto.GenreDto> genreDtos = genres.stream()
                                                    .map(g -> new BookDto.GenreDto(g.getId(), g.getName()))
                                                    .toList();
                                            return new BookDto(
                                                    saved.getId(),
                                                    saved.getTitle(),
                                                    author.getId(),
                                                    author.getFullName(),
                                                    genreDtos
                                            );
                                        });
                            });
                });
    }

    @Override
    public Mono<BookDto> update(Long id, BookUpdateDto updateDto) {
        return jdbcBookRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Book not found")))
                .flatMap(existingBook -> {
                    existingBook.setTitle(updateDto.getTitle());
                    existingBook.setAuthorId(updateDto.getAuthorId());
                    return jdbcBookRepository.save(existingBook);
                })
                .flatMap(savedBook ->
                        jdbcAuthorRepository.findById(updateDto.getAuthorId())
                                .switchIfEmpty(Mono.error(new EntityNotFoundException("Author not found")))
                                .zipWith(jdbcGenreRepository.findAllByIdIn(updateDto.getGenreIds().stream().toList())
                                        .collectList()
                                        .filter(list -> list.size() == updateDto.getGenreIds().size())
                                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Some genres not found")))
                                )
                                .flatMap(tuple -> {
                                    Author author = tuple.getT1();
                                    List<Genre> genres = tuple.getT2();
                                    Long bookId = savedBook.getId();

                                    return jdbcBookGenreRepository.deleteByBookId(bookId)
                                            .thenMany(Flux.fromIterable(genres)
                                                    .map(genre -> new BookGenre(bookId, genre.getId()))
                                                    .flatMap(jdbcBookGenreRepository::save)
                                            )
                                            .then(Mono.just(savedBook))
                                            .map(saved -> {
                                                List<BookDto.GenreDto> genreDtos = genres.stream()
                                                        .map(genre -> new BookDto.GenreDto(genre.getId(), genre.getName()))
                                                        .toList();
                                                return new BookDto(
                                                        saved.getId(),
                                                        saved.getTitle(),
                                                        author.getId(),
                                                        author.getFullName(),
                                                        genreDtos
                                                );
                                            });
                                })
                );
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return jdbcBookGenreRepository.deleteByBookId(id)
                .then(jdbcBookRepository.deleteById(id));
    }
}