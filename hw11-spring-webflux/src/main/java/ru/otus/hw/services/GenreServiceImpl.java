package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.JdbcGenreRepository;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final JdbcGenreRepository genreRepository;

    @Override
    public Flux<GenreDto> findAll() {
        return genreRepository.findAll()
                .map(this::toDto);
    }

    @Override
    public Mono<GenreDto> insert(GenreDto genreDto) {
        Genre genre = new Genre();
        genre.setName(genreDto.getName());
        return genreRepository.save(genre)
                .map(this::toDto);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return genreRepository.deleteById(id);
    }

    private GenreDto toDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }
}