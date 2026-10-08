package ru.otus.hw.hotel.services;

import ru.otus.hw.hotel.dto.CityDto;
import ru.otus.hw.hotel.dto.CreateCityRequest;

import java.util.List;

public interface CityService {

    List<CityDto> findAll();

    CityDto findById(Long id);

    CityDto create(CreateCityRequest request);

    void deleteById(Long id);
}