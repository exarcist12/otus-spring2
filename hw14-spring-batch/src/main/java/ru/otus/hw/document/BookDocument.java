package ru.otus.hw.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "books")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDocument {
    @Id
    private String id;

    private String title;

    private AuthorDocument author;

    private List<GenreDocument> genres = new ArrayList<>();

    private List<CommentDocument> comments = new ArrayList<>();

    public BookDocument(String title, AuthorDocument author, List<GenreDocument> genres, List<CommentDocument> comments) {
        this.title = title;
        this.author = author;
        this.genres = genres != null ? genres : new ArrayList<>();
        this.comments = comments != null ? comments : new ArrayList<>();
    }
}