package ru.yandex.practicum.filmorate.service.dao;

import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewService {

    Collection<ReviewDto> find(Integer filmId, Integer count);

    ReviewDto findReviewById(Long id);

    Review getReviewById(Long id);

    ReviewDto create(Review newReview);

    ReviewDto update(Review updReview);

    void delete(Long reviewId);

    boolean getUserLike(Long reviewId, Long userId);

    void addLike(Long reviewId, Long userId);

    void addDislike(Long reviewId, Long userId);

    void deleteLike(Long reviewId, Long userId);

    void deleteDislike(Long reviewId, Long userId);
}
