package ru.otus.hw.hotel.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.hotel.dto.CityDto;
import ru.otus.hw.hotel.dto.CreateCityRequest;
import ru.otus.hw.hotel.services.CityService;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping
    public List<CityDto> getAll() {
        return cityService.findAll();
    }

    @GetMapping("/{id}")
    public CityDto getById(@PathVariable Long id) {
        return cityService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CityDto create(@Valid @RequestBody CreateCityRequest request) {
        return cityService.create(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        cityService.deleteById(id);
    }
}