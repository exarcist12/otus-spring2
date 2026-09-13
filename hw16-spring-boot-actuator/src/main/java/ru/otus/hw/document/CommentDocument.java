package ru.otus.hw.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDocument {
    @Id
    private String id;

    private String text;

    public CommentDocument(String text) {
        this.text = text;
    }
}