package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.JdbcGenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final JdbcGenreRepository jdbcGenreRepository;

    @Override
    public List<Genre> findAll() {
        return jdbcGenreRepository.findAll();
    }
}
