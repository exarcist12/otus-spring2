package ru.otus.hw.hotel.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.hotel.dto.CreateReviewRequest;
import ru.otus.hw.hotel.dto.ReviewDto;
import ru.otus.hw.hotel.dto.UpdateReviewRequest;
import ru.otus.hw.hotel.models.User;
import ru.otus.hw.hotel.services.ReviewService;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public List<ReviewDto> getByHotel(@RequestParam Long hotelId) {
        return reviewService.findByHotelId(hotelId);
    }

    @GetMapping("/{id}")
    public ReviewDto getById(@PathVariable Long id) {
        return reviewService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto create(@Valid @RequestBody CreateReviewRequest request,
                            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return reviewService.create(request, currentUser.getId());
    }

    @PutMapping("/{id}")
    public ReviewDto update(@PathVariable Long id,
                            @Valid @RequestBody UpdateReviewRequest request,
                            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return reviewService.update(id, request, currentUser.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        reviewService.deleteById(id, currentUser.getId());
    }
}