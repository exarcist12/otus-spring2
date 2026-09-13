package ru.otus.hw.services;

import ru.otus.hw.dto.CommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> findByBookId(Long bookId);

    CommentDto findById(long id);

    CommentDto insert(CommentDto commentDto);

    void deleteById(long id);
}