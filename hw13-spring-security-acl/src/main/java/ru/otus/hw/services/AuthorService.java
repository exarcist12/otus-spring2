package ru.otus.hw.services;

import ru.otus.hw.dto.AuthorDto;

import java.util.List;

public interface AuthorService {

    List<AuthorDto> findAll();

    AuthorDto insert(AuthorDto authorDto);

    void deleteById(Long id);

    List<AuthorDto> findByUserId(Long userId);
}