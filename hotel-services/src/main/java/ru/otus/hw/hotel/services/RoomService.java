package ru.otus.hw.hotel.services;

import ru.otus.hw.hotel.dto.CreateRoomRequest;
import ru.otus.hw.hotel.dto.RoomDto;
import ru.otus.hw.hotel.dto.UpdateRoomRequest;

import java.util.List;

public interface RoomService {

    List<RoomDto> findByHotelId(Long hotelId);

    RoomDto findById(Long id);

    RoomDto create(CreateRoomRequest request);

    RoomDto update(Long id, UpdateRoomRequest request);

    void deleteById(Long id);
}