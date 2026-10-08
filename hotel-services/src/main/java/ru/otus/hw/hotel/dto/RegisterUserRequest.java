package ru.otus.hw.hotel.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {

    @NotBlank(message = "Логин обязателен")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "Пароль обязателен")
    @Size(min = 6, max = 100)
    private String password;

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    private String email;
}