package ru.otus.hw.hotel.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.hotel.dto.CityDto;
import ru.otus.hw.hotel.models.City;

import java.util.List;

@Component
public class CityMapper {

    public CityDto toDto(City city) {
        if (city == null) {
            return null;
        }
        return new CityDto(
                city.getId(),
                city.getName(),
                city.getCountry()
        );
    }

    public City toEntity(CityDto dto) {
        if (dto == null) {
            return null;
        }
        City city = new City();
        city.setId(dto.getId());
        city.setName(dto.getName());
        city.setCountry(dto.getCountry());
        return city;
    }

    public List<CityDto> toDtoList(List<City> cities) {
        return cities.stream()
                .map(this::toDto)
                .toList();
    }
}