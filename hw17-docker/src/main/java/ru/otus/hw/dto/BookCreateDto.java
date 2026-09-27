package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Set;

@Data
public class BookCreateDto {
    @NotBlank(message = "Название не может быть пустым")
    private String title;

    @NotNull(message = "Автор обязателен")
    private Long authorId;

    @NotNull(message = "Жанры обязательны")
    private Set<Long> genreIds;
}