package ru.otus.hw.services;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.User;

import java.util.List;

public interface AuthorService {

    List<AuthorDto> findAll();

    AuthorDto insert(AuthorDto authorDto);

    void deleteById(Long id);

    List<AuthorDto> findByUserId(Long userId);

    boolean isAuthorBelongsToUser(Long authorId, User user);
}