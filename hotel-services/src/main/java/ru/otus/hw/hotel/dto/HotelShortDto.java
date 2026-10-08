package ru.otus.hw.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelShortDto {
    private Long id;
    private String name;
    private Integer stars;
    private String cityName;
}