package ru.otus.hw.hotel.services;

import ru.otus.hw.hotel.dto.CreateReviewRequest;
import ru.otus.hw.hotel.dto.ReviewDto;
import ru.otus.hw.hotel.dto.UpdateReviewRequest;

import java.util.List;

public interface ReviewService {

    List<ReviewDto> findByHotelId(Long hotelId);

    ReviewDto findById(Long id);

    ReviewDto create(CreateReviewRequest request, Long userId);

    ReviewDto update(Long id, UpdateReviewRequest request, Long userId);

    void deleteById(Long id, Long userId);

    boolean hasUserReviewedHotel(Long userId, Long hotelId);
}