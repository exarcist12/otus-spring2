package ru.otus.hw.hotel.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.hotel.dto.CreateRoomRequest;
import ru.otus.hw.hotel.dto.RoomDto;
import ru.otus.hw.hotel.dto.UpdateRoomRequest;
import ru.otus.hw.hotel.services.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping
    public List<RoomDto> getByHotel(@RequestParam Long hotelId) {
        return roomService.findByHotelId(hotelId);
    }

    @GetMapping("/{id}")
    public RoomDto getById(@PathVariable Long id, Authentication authentication) {
        RoomDto dto = roomService.findById(id);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (!isAdmin) {
            dto.setCurrentBooking(null);
        }
        return dto;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public RoomDto create(@Valid @RequestBody CreateRoomRequest request) {
        return roomService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RoomDto update(@PathVariable Long id, @Valid @RequestBody UpdateRoomRequest request) {
        return roomService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        roomService.deleteById(id);
    }
}