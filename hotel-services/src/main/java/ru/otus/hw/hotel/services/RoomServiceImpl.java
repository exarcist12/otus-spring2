package ru.otus.hw.hotel.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.hotel.dto.CreateRoomRequest;
import ru.otus.hw.hotel.dto.RoomDto;
import ru.otus.hw.hotel.dto.UpdateRoomRequest;
import ru.otus.hw.hotel.enums.BookingStatus;
import ru.otus.hw.hotel.exceptions.EntityNotFoundException;
import ru.otus.hw.hotel.mappers.RoomMapper;
import ru.otus.hw.hotel.models.Booking;
import ru.otus.hw.hotel.models.Hotel;
import ru.otus.hw.hotel.models.Room;
import ru.otus.hw.hotel.repositories.BookingRepository;
import ru.otus.hw.hotel.repositories.HotelRepository;
import ru.otus.hw.hotel.repositories.RoomRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final HotelRepository hotelRepository;
    private final RoomMapper roomMapper;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoomDto> findByHotelId(Long hotelId) {
        return roomRepository.findByHotelId(hotelId).stream()
                .map(roomMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoomDto findById(Long id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room with id %d not found".formatted(id)));

        Optional<Booking> currentBooking = bookingRepository.findCurrentBookingForRoom(
                id,
                LocalDate.now(),
                List.of(BookingStatus.PENDING, BookingStatus.PAID)
        );

        return roomMapper.toDtoWithBooking(room, currentBooking.orElse(null));
    }

    @Override
    public RoomDto create(CreateRoomRequest request) {
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new EntityNotFoundException("Hotel with id %d not found".formatted(request.getHotelId())));

        Room room = new Room();
        room.setHotel(hotel);
        room.setType(request.getType());
        room.setPricePerNight(request.getPricePerNight());
        room.setCapacity(request.getCapacity());
        room.setDescription(request.getDescription());

        return roomMapper.toDto(roomRepository.save(room));
    }

    @Override
    public RoomDto update(Long id, UpdateRoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room with id %d not found".formatted(id)));

        room.setType(request.getType());
        room.setPricePerNight(request.getPricePerNight());
        room.setCapacity(request.getCapacity());
        room.setDescription(request.getDescription());

        return roomMapper.toDto(roomRepository.save(room));
    }

    @Override
    public void deleteById(Long id) {
        if (!roomRepository.existsById(id)) {
            throw new EntityNotFoundException("Room with id %d not found".formatted(id));
        }
        roomRepository.deleteById(id);
    }
}