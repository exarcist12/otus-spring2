package ru.otus.hw.commands;


import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.services.CommentService;

import java.util.stream.Collectors;

@ShellComponent
@RequiredArgsConstructor
public class CommentCommands {
    private final CommentService commentService;

    @ShellMethod(value = "Find comment by id", key = "cfid")
    public String findCommentById(long id) {
        return commentService.findById(id)
                .map(comment -> String.format(
                        "Id: %d, Text: %s, BookId: %d",
                        comment.getId(),
                        comment.getText(),
                        comment.getBook().getId()
                ))
                .orElse("Comment with id %d not found".formatted(id));
    }

    @ShellMethod(value = "Find all comments by book id", key = "cfbid")
    public String findCommentsByBookId(long bookId) {
        var comments = commentService.findByBookId(bookId);
        if (comments.isEmpty()) {
            return "No comments for book with id %d".formatted(bookId);
        }
        return comments.stream()
                .map(comment -> String.format(
                        "Id: %d, Text: %s",
                        comment.getId(),
                        comment.getText()
                ))
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod(value = "Insert comment", key = "cins")
    public String insertComment(String text, long bookId) {
        var comment = commentService.insert(text, bookId);
        return "Comment created: Id: %d, Text: %s, BookId: %d".formatted(
                comment.getId(),
                comment.getText(),
                comment.getBook().getId()
        );
    }

    @ShellMethod(value = "Update comment", key = "cupd")
    public String updateComment(long id, String text) {
        var comment = commentService.update(id, text);
        return "Comment updated: Id: %d, Text: %s".formatted(
                comment.getId(),
                comment.getText()
        );
    }

    @ShellMethod(value = "Delete comment", key = "cdel")
    public String deleteComment(long id) {
        commentService.deleteById(id);
        return "Comment with id %d deleted".formatted(id);
    }

}