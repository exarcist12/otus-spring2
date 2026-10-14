package ru.otus.hw.hotel.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.hotel.dto.AmenityDto;
import ru.otus.hw.hotel.models.Amenity;

import java.util.List;
import java.util.Set;

@Component
public class AmenityMapper {

    public AmenityDto toDto(Amenity amenity) {
        if (amenity == null) {
            return null;
        }
        return new AmenityDto(
                amenity.getId(),
                amenity.getName(),
                amenity.getIcon()
        );
    }

    public Amenity toEntity(AmenityDto dto) {
        if (dto == null) {
            return null;
        }
        Amenity amenity = new Amenity();
        amenity.setId(dto.getId());
        amenity.setName(dto.getName());
        amenity.setIcon(dto.getIcon());
        return amenity;
    }

    public List<AmenityDto> toDtoList(Set<Amenity> amenities) {
        return amenities.stream()
                .map(this::toDto)
                .toList();
    }
}