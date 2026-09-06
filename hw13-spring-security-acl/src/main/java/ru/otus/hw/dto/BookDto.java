package ru.otus.hw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;

    private String title;

    private Long authorId;

    private String authorName;

    private List<GenreDto> genres;

    @Data
    @AllArgsConstructor
    public static class GenreDto {
        private Long id;

        private String name;
    }
}