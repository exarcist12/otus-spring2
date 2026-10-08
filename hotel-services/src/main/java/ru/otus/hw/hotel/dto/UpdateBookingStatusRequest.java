package ru.otus.hw.hotel.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.otus.hw.hotel.enums.BookingStatus;

@Data
public class UpdateBookingStatusRequest {

    @NotNull(message = "Статус обязателен")
    private BookingStatus status;
}