package ru.otus.hw.hotel.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.hotel.dto.BookingDto;
import ru.otus.hw.hotel.models.Booking;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    private final UserMapper userMapper;
    private final RoomMapper roomMapper;

    public BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return new BookingDto(
                booking.getId(),
                userMapper.toDto(booking.getUser()),
                roomMapper.toShortDto(booking.getRoom()),
                booking.getRoom() != null && booking.getRoom().getHotel() != null
                        ? booking.getRoom().getHotel().getName()
                        : null,
                booking.getCheckIn(),
                booking.getCheckOut(),
                booking.getStatus(),
                booking.getTotalPrice(),
                booking.getPaymentId(),
                booking.getCreatedAt()
        );
    }

    public List<BookingDto> toDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(this::toDto)
                .toList();
    }
}