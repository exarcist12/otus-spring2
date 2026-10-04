package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.JdbcGenreRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final JdbcGenreRepository jdbcGenreRepository;

    @Override
    @Transactional(readOnly = true)
    public List<GenreDto> findAll() {
        return jdbcGenreRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public GenreDto insert(GenreDto genreDto) {
        Genre genre = new Genre();
        genre.setName(genreDto.getName());
        Genre saved = jdbcGenreRepository.save(genre);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jdbcGenreRepository.deleteById(id);
    }

    private GenreDto toDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}