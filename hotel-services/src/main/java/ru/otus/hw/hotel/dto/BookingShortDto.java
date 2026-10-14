package ru.otus.hw.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.hotel.enums.BookingStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingShortDto {
    private Long id;
    private String username;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private BookingStatus status;
}