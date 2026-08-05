package ru.otus.hw.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

@RestController
@RequestMapping("/api/books/{bookId}/comments")
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;

    @GetMapping
    public Flux<CommentDto> getComments(@PathVariable Long bookId) {
        return commentService.findByBookId(bookId);
    }

    @PostMapping
    public Mono<CommentDto> createComment(@PathVariable Long bookId, @RequestBody CommentDto commentDto) {
        commentDto.setBookId(bookId);
        return commentService.insert(commentDto);
    }

    @DeleteMapping("/{id}")
    public Mono<Void> deleteComment(@PathVariable Long id) {
        return commentService.deleteById(id);
    }
}