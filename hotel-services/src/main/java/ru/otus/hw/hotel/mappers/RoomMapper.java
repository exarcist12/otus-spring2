package ru.otus.hw.hotel.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.hotel.dto.BookingShortDto;
import ru.otus.hw.hotel.dto.RoomDto;
import ru.otus.hw.hotel.dto.RoomShortDto;
import ru.otus.hw.hotel.enums.RoomAvailability;
import ru.otus.hw.hotel.models.Booking;
import ru.otus.hw.hotel.models.Room;

import java.util.List;

@Component
public class RoomMapper {

    public RoomDto toDto(Room room) {
        if (room == null) {
            return null;
        }

        RoomDto dto = new RoomDto();
        dto.setId(room.getId());
        dto.setHotelId(room.getHotel() != null ? room.getHotel().getId() : null);
        dto.setHotelName(room.getHotel() != null ? room.getHotel().getName() : null);
        dto.setType(room.getType());
        dto.setPricePerNight(room.getPricePerNight());
        dto.setCapacity(room.getCapacity());
        dto.setDescription(room.getDescription());
        return dto;
    }

    public RoomDto toDtoWithBooking(Room room, Booking currentBooking) {
        RoomDto dto = toDto(room);
        if (dto == null) {
            return null;
        }

        if (currentBooking != null) {
            dto.setAvailability(RoomAvailability.BOOKED);
            dto.setCurrentBooking(new BookingShortDto(
                    currentBooking.getId(),
                    currentBooking.getUser().getUsername(),
                    currentBooking.getCheckIn(),
                    currentBooking.getCheckOut(),
                    currentBooking.getStatus()
            ));
        } else {
            dto.setAvailability(RoomAvailability.AVAILABLE);
            dto.setCurrentBooking(null);
        }
        return dto;
    }

    public RoomShortDto toShortDto(Room room) {
        if (room == null) {
            return null;
        }
        return new RoomShortDto(
                room.getId(),
                room.getType(),
                room.getPricePerNight(),
                room.getCapacity()
        );
    }

    public List<RoomDto> toDtoList(List<Room> rooms) {
        return rooms.stream()
                .map(this::toDto)
                .toList();
    }
}