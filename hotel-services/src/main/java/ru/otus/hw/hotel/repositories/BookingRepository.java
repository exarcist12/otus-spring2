package ru.otus.hw.hotel.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.otus.hw.hotel.enums.BookingStatus;
import ru.otus.hw.hotel.models.Booking;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"user", "room", "room.hotel"})
    List<Booking> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"user", "room", "room.hotel"})
    List<Booking> findByRoomId(Long roomId);

    List<Booking> findByStatus(BookingStatus status);

    @Query("""
        SELECT b FROM Booking b
        WHERE b.room.id = :roomId
          AND b.status IN :statuses
          AND b.checkIn < :checkOut
          AND b.checkOut > :checkIn
    """)
    List<Booking> findOverlappingBookings(
            @Param("roomId") Long roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut,
            @Param("statuses") Collection<BookingStatus> statuses
    );

    @Query("""
    SELECT b FROM Booking b
    WHERE b.room.id = :roomId
      AND b.status IN :statuses
      AND b.checkIn <= :today
      AND b.checkOut > :today
""")
    Optional<Booking> findCurrentBookingForRoom(
            @Param("roomId") Long roomId,
            @Param("today") LocalDate today,
            @Param("statuses") Collection<BookingStatus> statuses
    );

    @EntityGraph(attributePaths = {"user", "room", "room.hotel"})
    @Query("SELECT b FROM Booking b ORDER BY b.createdAt DESC")
    List<Booking> findAllWithDetails();
}