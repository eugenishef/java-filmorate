package ru.yandex.practicum.filmorate.dal.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Optional;

public interface ReviewStorage {
    Collection<Review> find(Integer filmId, Integer count);

    Review create(Review newReview);

    Review update(Review updReview);

    void delete(Long reviewId);

    Optional<Review> findReviewById(Long id);

    void addLike(Long reviewId, Long userId, Boolean isUseful);

    void deleteLike(Long reviewId, Long userId);

    Optional<Boolean> getUserLikeOrDislike(Long reviewId, Long userId);
}
