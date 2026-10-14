package ru.otus.hw.hotel.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.hotel.enums.RoomType;
import ru.otus.hw.hotel.models.Room;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @EntityGraph(attributePaths = {"hotel"})
    List<Room> findByHotelId(Long hotelId);

    @EntityGraph(attributePaths = {"hotel"})
    List<Room> findByHotelIdAndType(Long hotelId, RoomType type);
}