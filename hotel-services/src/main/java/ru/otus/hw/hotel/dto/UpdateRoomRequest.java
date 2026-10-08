package ru.otus.hw.hotel.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import ru.otus.hw.hotel.enums.RoomType;

import java.math.BigDecimal;

@Data
public class UpdateRoomRequest {

    @NotNull(message = "Тип номера обязателен")
    private RoomType type;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal pricePerNight;

    @NotNull(message = "Вместимость обязательна")
    @Min(1)
    private Integer capacity;

    @Size(max = 1000)
    private String description;
}