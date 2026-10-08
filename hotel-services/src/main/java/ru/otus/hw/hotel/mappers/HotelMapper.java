package ru.otus.hw.hotel.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.hotel.dto.HotelDto;
import ru.otus.hw.hotel.dto.HotelShortDto;
import ru.otus.hw.hotel.models.Hotel;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HotelMapper {

    private final CityMapper cityMapper;
    private final AmenityMapper amenityMapper;

    public HotelDto toDto(Hotel hotel) {
        if (hotel == null) {
            return null;
        }

        return new HotelDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getDescription(),
                hotel.getAddress(),
                hotel.getStars(),
                cityMapper.toDto(hotel.getCity()),
                amenityMapper.toDtoList(hotel.getAmenities())
        );
    }

    public HotelShortDto toShortDto(Hotel hotel) {
        if (hotel == null) {
            return null;
        }

        return new HotelShortDto(
                hotel.getId(),
                hotel.getName(),
                hotel.getStars(),
                hotel.getCity() != null ? hotel.getCity().getName() : null
        );
    }

    public List<HotelDto> toDtoList(List<Hotel> hotels) {
        return hotels.stream()
                .map(this::toDto)
                .toList();
    }

    public List<HotelShortDto> toShortDtoList(List<Hotel> hotels) {
        return hotels.stream()
                .map(this::toShortDto)
                .toList();
    }
}