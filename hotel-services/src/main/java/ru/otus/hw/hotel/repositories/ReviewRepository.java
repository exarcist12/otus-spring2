package ru.otus.hw.hotel.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.hotel.models.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @EntityGraph(attributePaths = {"user", "hotel"})
    List<Review> findByHotelId(Long hotelId);

    @EntityGraph(attributePaths = {"user", "hotel"})
    List<Review> findByUserId(Long userId);

    boolean existsByUserIdAndHotelId(Long userId, Long hotelId);

    long countByHotelId(Long hotelId);
}