package ru.otus.hw.hotel.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UpdateReviewRequest {

    @NotBlank(message = "Текст отзыва обязателен")
    @Size(max = 5000)
    private String text;

    @NotNull(message = "Оценка обязательна")
    @Min(1) @Max(5)
    private Integer rating;
}