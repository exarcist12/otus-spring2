package ru.otus.hw.hotel.services;

import ru.otus.hw.hotel.dto.AmenityDto;
import ru.otus.hw.hotel.dto.CreateAmenityRequest;

import java.util.List;

public interface AmenityService {

    List<AmenityDto> findAll();

    AmenityDto findById(Long id);

    AmenityDto create(CreateAmenityRequest request);

    void deleteById(Long id);
}