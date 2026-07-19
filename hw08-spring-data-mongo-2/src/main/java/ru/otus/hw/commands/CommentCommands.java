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
    public String findCommentById(String id) {
        return commentService.findById(id)
                .map(comment -> String.format(
                        "Id: %s, Text: %s, BookId: %s",
                        comment.getId(),
                        comment.getText(),
                        comment.getBookId()
                ))
                .orElse("Comment with id %s not found".formatted(id));
    }

    @ShellMethod(value = "Find all comments by book id", key = "cfbid")
    public String findCommentsByBookId(String bookId) {
        var comments = commentService.findByBookId(bookId);
        if (comments.isEmpty()) {
            return "No comments for book with id %s".formatted(bookId);
        }
        return comments.stream()
                .map(comment -> String.format(
                        "Id: %s, Text: %s",
                        comment.getId(),
                        comment.getText()
                ))
                .collect(Collectors.joining("\n"));
    }

    @ShellMethod(value = "Insert comment", key = "cins")
    public String insertComment(String text, String bookId) {
        var comment = commentService.insert(text, bookId);
        return "Comment created: Id: %s, Text: %s, BookId: %s".formatted(
                comment.getId(),
                comment.getText(),
                comment.getBookId()
        );
    }

    @ShellMethod(value = "Update comment", key = "cupd")
    public String updateComment(String id, String text) {
        var comment = commentService.update(id, text);
        return "Comment updated: Id: %s, Text: %s".formatted(
                comment.getId(),
                comment.getText()
        );
    }

    @ShellMethod(value = "Delete comment", key = "cdel")
    public String deleteComment(String id) {
        commentService.deleteById(id);
        return "Comment with id %s deleted".formatted(id);
    }

}