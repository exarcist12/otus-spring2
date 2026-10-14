package ru.otus.hw.hotel.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.hotel.dto.BookingDto;
import ru.otus.hw.hotel.dto.CreateBookingRequest;
import ru.otus.hw.hotel.dto.UpdateBookingStatusRequest;
import ru.otus.hw.hotel.models.User;
import ru.otus.hw.hotel.services.BookingService;
import ru.otus.hw.hotel.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserService userService;

    @GetMapping("/my")
    public List<BookingDto> getMyBookings(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return bookingService.findByUserId(currentUser.getId());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @bookingSecurity.isOwner(#id, authentication.principal.id)")
    public BookingDto getById(@PathVariable Long id) {
        return bookingService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@Valid @RequestBody CreateBookingRequest request,
                             Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return bookingService.create(request, currentUser.getId());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public BookingDto updateStatus(@PathVariable Long id,
                                   @Valid @RequestBody UpdateBookingStatusRequest request) {
        return bookingService.updateStatus(id, request);
    }
}