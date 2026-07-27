package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class BookForm {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String title;

    private Long authorId;

    private List<Long> genreIds;
}