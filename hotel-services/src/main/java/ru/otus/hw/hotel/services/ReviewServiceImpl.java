package ru.otus.hw.hotel.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.hotel.dto.CreateReviewRequest;
import ru.otus.hw.hotel.dto.ReviewDto;
import ru.otus.hw.hotel.dto.UpdateReviewRequest;
import ru.otus.hw.hotel.exceptions.EntityNotFoundException;
import ru.otus.hw.hotel.mappers.ReviewMapper;
import ru.otus.hw.hotel.models.Hotel;
import ru.otus.hw.hotel.models.Review;
import ru.otus.hw.hotel.models.User;
import ru.otus.hw.hotel.repositories.HotelRepository;
import ru.otus.hw.hotel.repositories.ReviewRepository;
import ru.otus.hw.hotel.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDto> findByHotelId(Long hotelId) {
        return reviewMapper.toDtoList(reviewRepository.findByHotelId(hotelId));
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDto findById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review with id %d not found".formatted(id)));
        return reviewMapper.toDto(review);
    }

    @Override
    public ReviewDto create(CreateReviewRequest request, Long userId) {
        if (reviewRepository.existsByUserIdAndHotelId(userId, request.getHotelId())) {
            throw new IllegalArgumentException("You have already reviewed this hotel");
        }

        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new EntityNotFoundException("Hotel with id %d not found".formatted(request.getHotelId())));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(userId)));

        Review review = new Review();
        review.setUser(user);
        review.setHotel(hotel);
        review.setText(request.getText());
        review.setRating(request.getRating());

        return reviewMapper.toDto(reviewRepository.save(review));
    }

    @Override
    public ReviewDto update(Long id, UpdateReviewRequest request, Long userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review with id %d not found".formatted(id)));

        checkOwnership(review, userId);

        review.setText(request.getText());
        review.setRating(request.getRating());

        return reviewMapper.toDto(reviewRepository.save(review));
    }

    @Override
    public void deleteById(Long id, Long userId) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review with id %d not found".formatted(id)));

        checkOwnership(review, userId);

        reviewRepository.deleteById(id);
    }

    private void checkOwnership(Review review, Long userId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if (!isAdmin && !review.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You can only edit your own reviews");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReviewedHotel(Long userId, Long hotelId) {
        return reviewRepository.existsByUserIdAndHotelId(userId, hotelId);
    }
}