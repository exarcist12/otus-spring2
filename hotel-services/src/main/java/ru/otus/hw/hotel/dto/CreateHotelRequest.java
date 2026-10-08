package ru.otus.hw.hotel.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.Set;

@Data
public class CreateHotelRequest {

    @NotBlank(message = "Название обязательно")
    @Size(max = 255)
    private String name;

    @Size(max = 5000)
    private String description;

    @NotBlank(message = "Адрес обязателен")
    @Size(max = 500)
    private String address;

    @NotNull(message = "Количество звёзд обязательно")
    @Min(1) @Max(5)
    private Integer stars;

    @NotNull(message = "Город обязателен")
    private Long cityId;

    private Set<Long> amenityIds;
}