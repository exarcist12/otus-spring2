package ru.otus.hw.hotel.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.hotel.dto.ReviewDto;
import ru.otus.hw.hotel.models.Review;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewMapper {

    private final UserMapper userMapper;

    public ReviewDto toDto(Review review) {
        if (review == null) {
            return null;
        }

        return new ReviewDto(
                review.getId(),
                userMapper.toDto(review.getUser()),
                review.getHotel() != null ? review.getHotel().getId() : null,
                review.getHotel() != null ? review.getHotel().getName() : null,
                review.getText(),
                review.getRating(),
                review.getCreatedAt()
        );
    }

    public List<ReviewDto> toDtoList(List<Review> reviews) {
        return reviews.stream()
                .map(this::toDto)
                .toList();
    }
}