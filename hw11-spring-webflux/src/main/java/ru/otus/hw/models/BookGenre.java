package ru.otus.hw.models;

import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("books_genres")
public class BookGenre {
    private Long bookId;

    private Long genreId;
}