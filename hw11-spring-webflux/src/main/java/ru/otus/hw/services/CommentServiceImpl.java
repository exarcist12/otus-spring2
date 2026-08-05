package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.JdbcBookRepository;
import ru.otus.hw.repositories.JdbcCommentRepository;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final JdbcCommentRepository commentRepository;
    private final JdbcBookRepository bookRepository;

    @Override
    public Flux<CommentDto> findByBookId(Long bookId) {
        return commentRepository.findByBookId(bookId)
                .map(this::toDto);
    }

    @Override
    public Mono<CommentDto> insert(CommentDto commentDto) {
        return bookRepository.existsById(commentDto.getBookId())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new EntityNotFoundException("Book not found"));
                    }
                    Comment comment = new Comment();
                    comment.setText(commentDto.getText());
                    comment.setBookId(commentDto.getBookId());
                    return commentRepository.save(comment)
                            .map(this::toDto);
                });
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return commentRepository.deleteById(id);
    }

    private CommentDto toDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getBookId());
    }
}
