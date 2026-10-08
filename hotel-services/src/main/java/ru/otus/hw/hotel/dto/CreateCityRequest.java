package ru.otus.hw.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCityRequest {

    @NotBlank(message = "Название города обязательно")
    @Size(max = 255)
    private String name;

    @NotBlank(message = "Страна обязательна")
    @Size(max = 255)
    private String country;
}