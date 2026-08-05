package ru.otus.hw.repositories;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookWithDetails {
    private Long bookId;

    private String bookTitle;

    private Long authorId;

    private String authorName;

    private Long genreId;

    private String genreName;
}