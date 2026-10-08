package ru.otus.hw.hotel.services;

import ru.otus.hw.hotel.dto.BookingDto;
import ru.otus.hw.hotel.dto.CreateBookingRequest;
import ru.otus.hw.hotel.dto.UpdateBookingStatusRequest;

import java.util.List;

public interface BookingService {

    List<BookingDto> findByUserId(Long userId);

    BookingDto findById(Long id);

    BookingDto create(CreateBookingRequest request, Long userId);

    BookingDto updateStatus(Long id, UpdateBookingStatusRequest request);
}