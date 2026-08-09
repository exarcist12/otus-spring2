package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.JdbcAuthorRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final JdbcAuthorRepository jdbcAuthorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AuthorDto> findAll() {
        return jdbcAuthorRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AuthorDto insert(AuthorDto authorDto) {
        Author author = new Author();
        author.setFullName(authorDto.getFullName());
        Author saved = jdbcAuthorRepository.save(author);
        return toDto(saved);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jdbcAuthorRepository.deleteById(id);
    }

    private AuthorDto toDto(Author author) {
        return new AuthorDto(author.getId(), author.getFullName());
    }
}