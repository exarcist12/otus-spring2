package ru.otus.hw.hotel.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.hotel.models.Hotel;

import java.util.List;
import java.util.Optional;

public interface HotelRepository extends JpaRepository<Hotel, Long> {

    @EntityGraph(attributePaths = {"city", "amenities"})
    @Query("SELECT h FROM Hotel h WHERE h.id = :id")
    Optional<Hotel> findByIdWithDetails(@Param("id") Long id);

    @EntityGraph(attributePaths = {"city", "amenities"})
    @Query("SELECT h FROM Hotel h")
    List<Hotel> findAllWithDetails();

    @EntityGraph(attributePaths = {"city", "amenities"})
    @Query("SELECT h FROM Hotel h WHERE h.city.id = :cityId")
    List<Hotel> findByCityIdWithDetails(@Param("cityId") Long cityId);

    List<Hotel> findByCityId(Long cityId);
}