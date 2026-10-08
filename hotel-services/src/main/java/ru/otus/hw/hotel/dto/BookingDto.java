package ru.otus.hw.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.hotel.enums.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {
    private Long id;
    private UserDto user;
    private RoomShortDto room;
    private String hotelName;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private BookingStatus status;
    private BigDecimal totalPrice;
    private String paymentId;
    private LocalDateTime createdAt;
}