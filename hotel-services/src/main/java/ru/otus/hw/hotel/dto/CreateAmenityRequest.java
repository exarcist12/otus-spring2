package ru.otus.hw.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAmenityRequest {

    @NotBlank(message = "Название удобства обязательно")
    @Size(max = 255)
    private String name;

    @Size(max = 255)
    private String icon;
}