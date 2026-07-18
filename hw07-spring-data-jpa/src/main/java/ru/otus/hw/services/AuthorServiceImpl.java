package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.JdbcAuthorRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final JdbcAuthorRepository jdbcAuthorRepository;

    @Override
    public List<Author> findAll() {
        return jdbcAuthorRepository.findAll();
    }
}
