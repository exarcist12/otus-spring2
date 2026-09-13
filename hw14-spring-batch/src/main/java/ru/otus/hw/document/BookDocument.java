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

    private String authorId;

    private List<String> genreIds = new ArrayList<>();


    public BookDocument(String title, String authorId, List<String> genreIds) {
        this.title = title;
        this.authorId = authorId;
        this.genreIds = genreIds != null ? genreIds : new ArrayList<>();
    }
}