package ru.otus.hw.services;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.JdbcBookRepository;
import ru.otus.hw.repositories.JdbcCommentRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentServiceImpl implements CommentService {

    private final JdbcCommentRepository jdbcCommentRepository;

    private final JdbcBookRepository jdbcBookRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> findById(long id) {
        return jdbcCommentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> findByBookId(long bookId) {
        return jdbcCommentRepository.findByBookId(bookId);
    }

    @Override
    public Comment insert(String text, long bookId) {
        var book = jdbcBookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));

        var comment = new Comment();
        comment.setText(text);
        comment.setBook(book);

        return jdbcCommentRepository.save(comment);
    }

    @Override
    public Comment update(long id, String text) {
        var comment = jdbcCommentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %d not found".formatted(id)));

        comment.setText(text);
        return jdbcCommentRepository.save(comment);
    }

    @Override
    public void deleteById(long id) {
        jdbcCommentRepository.deleteById(id);
    }
}