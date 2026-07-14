package ru.otus.hw.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;

@Document(collection = "books")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Book {
    @Id
    private String id;

    @Field("title")
    private String title;

    @Field("author")
    private Author author;

    @Field("genres")
    private List<Genre> genres = new ArrayList<>();

    @Field("comments")
    private List<Comment> comments = new ArrayList<>();

    @Override
    public String toString() {
        return "Book{id='" + id + "', title='" + title + "'}";
    }
}