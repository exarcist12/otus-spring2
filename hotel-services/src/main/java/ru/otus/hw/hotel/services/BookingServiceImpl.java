package ru.otus.hw.hotel.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.hotel.dto.BookingDto;
import ru.otus.hw.hotel.dto.CreateBookingRequest;
import ru.otus.hw.hotel.dto.PaymentRequest;
import ru.otus.hw.hotel.dto.PaymentResponse;
import ru.otus.hw.hotel.dto.UpdateBookingStatusRequest;
import ru.otus.hw.hotel.enums.BookingStatus;
import ru.otus.hw.hotel.exceptions.EntityNotFoundException;
import ru.otus.hw.hotel.exceptions.RoomNotAvailableException;
import ru.otus.hw.hotel.mappers.BookingMapper;
import ru.otus.hw.hotel.models.Booking;
import ru.otus.hw.hotel.models.Room;
import ru.otus.hw.hotel.models.User;
import ru.otus.hw.hotel.repositories.BookingRepository;
import ru.otus.hw.hotel.repositories.RoomRepository;
import ru.otus.hw.hotel.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;
    private final PaymentClient paymentClient;

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findByUserId(Long userId) {
        return bookingMapper.toDtoList(bookingRepository.findByUserId(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto findById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking with id %d not found".formatted(id)));
        return bookingMapper.toDto(booking);
    }
    @Override
    public BookingDto cancel(Long id, Long userId) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking with id %d not found".formatted(id)));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !booking.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("Нельзя отменить чужую бронь");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED
                || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new IllegalArgumentException("Бронь уже нельзя отменить");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return bookingMapper.toDto(bookingRepository.save(booking));
    }
    @Override
    public BookingDto create(CreateBookingRequest request, Long userId) {
        if (!request.getCheckIn().isBefore(request.getCheckOut())) {
            throw new IllegalArgumentException("Check-in must be before check-out");
        }

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new EntityNotFoundException("Room with id %d not found".formatted(request.getRoomId())));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(userId)));

        var overlapping = bookingRepository.findOverlappingBookings(
                request.getRoomId(),
                request.getCheckIn(),
                request.getCheckOut(),
                List.of(BookingStatus.PENDING, BookingStatus.PAID)
        );
        if (!overlapping.isEmpty()) {
            throw new RoomNotAvailableException("Room is not available for these dates");
        }

        long nights = ChronoUnit.DAYS.between(request.getCheckIn(), request.getCheckOut());
        BigDecimal totalPrice = room.getPricePerNight().multiply(BigDecimal.valueOf(nights));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setCheckIn(request.getCheckIn());
        booking.setCheckOut(request.getCheckOut());
        booking.setStatus(BookingStatus.PENDING);
        booking.setTotalPrice(totalPrice);

        Booking saved = bookingRepository.save(booking);

        PaymentRequest paymentRequest = new PaymentRequest(saved.getId(), totalPrice, "RUB", null);
        PaymentResponse paymentResponse = paymentClient.processPayment(paymentRequest);

        if ("SUCCESS".equals(paymentResponse.getStatus())) {
            saved.setStatus(BookingStatus.PAID);
            saved.setPaymentId(paymentResponse.getPaymentId());
            saved = bookingRepository.save(saved);
        } else {
            saved.setStatus(BookingStatus.CANCELLED);
            saved = bookingRepository.save(saved);
        }

        return bookingMapper.toDto(saved);
    }

    @Override
    public BookingDto updateStatus(Long id, UpdateBookingStatusRequest request) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking with id %d not found".formatted(id)));
        booking.setStatus(request.getStatus());
        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> findAll() {
        return bookingMapper.toDtoList(bookingRepository.findAllWithDetails());
    }

}