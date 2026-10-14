package ru.otus.hw.hotel.services;

import ru.otus.hw.hotel.dto.CreateHotelRequest;
import ru.otus.hw.hotel.dto.HotelDto;
import ru.otus.hw.hotel.dto.HotelShortDto;
import ru.otus.hw.hotel.dto.UpdateHotelRequest;

import java.util.List;

public interface HotelService {

    List<HotelShortDto> findAll();

    List<HotelShortDto> findByCityId(Long cityId);

    HotelDto findById(Long id);

    HotelDto create(CreateHotelRequest request);

    HotelDto update(Long id, UpdateHotelRequest request);

    void deleteById(Long id);
}