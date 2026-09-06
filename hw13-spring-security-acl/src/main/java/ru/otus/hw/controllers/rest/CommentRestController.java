package ru.otus.hw.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/books/{bookId}/comments")
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;

    @GetMapping
    public List<CommentDto> getComments(@PathVariable Long bookId) {
        return commentService.findByBookId(bookId);
    }

    @PostMapping
    public CommentDto createComment(@PathVariable Long bookId, @RequestBody CommentDto commentDto) {
        commentDto.setBookId(bookId);
        return commentService.insert(commentDto);
    }

}