package ru.otus.hw.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private Long id;
    private UserDto user;
    private Long hotelId;
    private String hotelName;
    private String text;
    private Integer rating;
    private LocalDateTime createdAt;
}