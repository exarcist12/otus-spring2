package ru.otus.hw.models;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "comments")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Comment {
    @Id
    private String id;

    private String text;

    @DBRef(lazy = true)
    private Book book;

    @Override
    public String toString() {
        return "Comment{id='" + id + "', text='" + text + "''}";
    }
}