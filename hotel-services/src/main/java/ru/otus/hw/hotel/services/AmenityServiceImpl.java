package ru.otus.hw.hotel.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.hotel.dto.AmenityDto;
import ru.otus.hw.hotel.dto.CreateAmenityRequest;
import ru.otus.hw.hotel.exceptions.EntityNotFoundException;
import ru.otus.hw.hotel.mappers.AmenityMapper;
import ru.otus.hw.hotel.models.Amenity;
import ru.otus.hw.hotel.repositories.AmenityRepository;
import ru.otus.hw.hotel.services.AmenityService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;
    private final AmenityMapper amenityMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AmenityDto> findAll() {
        return amenityRepository.findAll().stream()
                .map(amenityMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AmenityDto findById(Long id) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Amenity with id %d not found".formatted(id)));
        return amenityMapper.toDto(amenity);
    }

    @Override
    public AmenityDto create(CreateAmenityRequest request) {
        Amenity amenity = new Amenity();
        amenity.setName(request.getName());
        amenity.setIcon(request.getIcon());
        Amenity saved = amenityRepository.save(amenity);
        return amenityMapper.toDto(saved);
    }

    @Override
    public void deleteById(Long id) {
        if (!amenityRepository.existsById(id)) {
            throw new EntityNotFoundException("Amenity with id %d not found".formatted(id));
        }
        amenityRepository.deleteById(id);
    }
}