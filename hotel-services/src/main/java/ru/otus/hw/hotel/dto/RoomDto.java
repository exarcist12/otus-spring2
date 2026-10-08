package ru.otus.hw.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.otus.hw.hotel.enums.RoomAvailability;
import ru.otus.hw.hotel.enums.RoomType;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto {
    private Long id;
    private Long hotelId;
    private String hotelName;
    private RoomType type;
    private BigDecimal pricePerNight;
    private Integer capacity;
    private String description;
    private RoomAvailability availability;
    private BookingShortDto currentBooking;
}