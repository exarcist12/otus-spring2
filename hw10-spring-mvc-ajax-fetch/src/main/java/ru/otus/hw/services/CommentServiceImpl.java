package ru.otus.hw.services;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.JdbcCommentRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final JdbcCommentRepository jdbcCommentRepository;

    @Override
    public List<CommentDto> findByBookId(Long bookId) {
        return jdbcCommentRepository.findByBookId(bookId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto findById(long id) {
        Comment comment = jdbcCommentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        return toDto(comment);
    }

    @Override
    public CommentDto insert(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        Comment saved = jdbcCommentRepository.save(comment);
        return toDto(saved);
    }

    private CommentDto toDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getBook().getId()
        );
    }

    @Override
    public void deleteById(long id) {
        jdbcCommentRepository.deleteById(id);
    }
}