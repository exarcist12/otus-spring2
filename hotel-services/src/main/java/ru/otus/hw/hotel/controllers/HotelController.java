package ru.otus.hw.hotel.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.hotel.dto.CreateHotelRequest;
import ru.otus.hw.hotel.dto.HotelDto;
import ru.otus.hw.hotel.dto.HotelShortDto;
import ru.otus.hw.hotel.dto.UpdateHotelRequest;
import ru.otus.hw.hotel.services.HotelService;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @GetMapping
    public List<HotelShortDto> getAll(@RequestParam(required = false) Long cityId) {
        if (cityId != null) {
            return hotelService.findByCityId(cityId);
        }
        return hotelService.findAll();
    }

    @GetMapping("/{id}")
    public HotelDto getById(@PathVariable Long id) {
        return hotelService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public HotelDto create(@Valid @RequestBody CreateHotelRequest request) {
        return hotelService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HotelDto update(@PathVariable Long id, @Valid @RequestBody UpdateHotelRequest request) {
        return hotelService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        hotelService.deleteById(id);
    }
}