package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.JdbcAuthorRepository;
import ru.otus.hw.repositories.JdbcBookRepository;
import ru.otus.hw.repositories.JdbcGenreRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
@Transactional
public class BookServiceImpl implements BookService {
    private final JdbcAuthorRepository jdbcAuthorRepository;

    private final JdbcGenreRepository jdbcGenreRepository;

    private final JdbcBookRepository jdbcBookRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Book> findById(long id) {
        return jdbcBookRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Book> findAll() {
        return jdbcBookRepository.findAll();
    }

    @Override
    public Book insert(String title, long authorId, Set<Long> genresIds) {
        return save(0, title, authorId, genresIds);
    }

    @Override
    public Book update(long id, String title, long authorId, Set<Long> genresIds) {
        if (!jdbcBookRepository.existsById(id)) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(id));
        }
        return save(id, title, authorId, genresIds);
    }

    @Override
    public void deleteById(long id) {
        jdbcBookRepository.deleteById(id);
    }

    private Book save(long id, String title, long authorId, Set<Long> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = jdbcAuthorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %d not found".formatted(authorId)));
        var genres = jdbcGenreRepository.findAllByIds(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        var book = new Book(id, title, author, genres);
        return jdbcBookRepository.save(book);
    }
}
