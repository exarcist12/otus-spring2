package ru.otus.hw.hotel.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.hotel.dto.AmenityDto;
import ru.otus.hw.hotel.dto.CreateAmenityRequest;
import ru.otus.hw.hotel.services.AmenityService;

import java.util.List;

@RestController
@RequestMapping("/api/amenities")
@RequiredArgsConstructor
public class AmenityController {

    private final AmenityService amenityService;

    @GetMapping
    public List<AmenityDto> getAll() {
        return amenityService.findAll();
    }

    @GetMapping("/{id}")
    public AmenityDto getById(@PathVariable Long id) {
        return amenityService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public AmenityDto create(@Valid @RequestBody CreateAmenityRequest request) {
        return amenityService.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        amenityService.deleteById(id);
    }
}