package ru.yandex.practicum.filmorate.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.dao.EventStorage;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dal.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.EntityType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.dao.FilmService;
import ru.yandex.practicum.filmorate.service.dao.ReviewService;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.service.dao.UserService;
import ru.yandex.practicum.filmorate.validation.ReviewValidator;

import java.util.Collection;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    final ReviewStorage reviewStorage;
    final UserService userService;
    final FilmService filmService;
    final EventStorage eventStorage;

    static final String REVIEW_NOT_FOUND_MSG = "Отзыв с id = %d не найден";
    static final String LIKE_ADDED_MSG = "Добавлен лайк пользователя c id = {} к отзыву с reviewId = {}";
    static final String DISLIKE_ADDED_MSG = "Добавлен дизлайк пользователя c id = {} к отзыву с reviewId = {}";
    static final String DISLIKE_NOT_FOUND = "У пользователя с id = %d не найден дизлайк к отзыву с id = %d";
    static final String LIKE_OR_DISLIKE_NOT_FOUND = "У пользователя с id = %d нет ни лайка ни дизлайка к отзыву с id = %d\"";

    private <T> T getDefaultIfNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    @Override
    public Collection<ReviewDto> find(Integer filmId, Integer count) {
        Collection<Review> foundReviews = reviewStorage.find(filmId, count);
        return foundReviews.stream().map(ReviewMapper::modelToDto).collect(Collectors.toSet());
    }

    @Override
    public ReviewDto findReviewById(Long id) {
        return ReviewMapper.modelToDto(getReviewById(id));
    }

    @Override
    public Review getReviewById(Long id) {
        return reviewStorage.findReviewById(id)
                .orElseThrow(() -> new NotFoundException(String.format(REVIEW_NOT_FOUND_MSG, id)));
    }

    @Override
    public ReviewDto create(Review newReview) {
        ReviewValidator.validateNull(newReview);
        User user = userService.getUserById(newReview.getUserId());
        FilmDto filmDto = filmService.findFilmById(newReview.getFilmId());
        Review review = reviewStorage.create(newReview);
        eventStorage.add(user.getId(), Long.valueOf(review.getFilmId()), EntityType.REVIEW, Operation.ADD);
        return ReviewMapper.modelToDto(review);
    }

    @Override
    public ReviewDto update(Review updReview) {
        User user = userService.getUserById(updReview.getUserId());
        FilmDto filmDto = filmService.findFilmById(updReview.getFilmId());

        Review oldReview = getReviewById(updReview.getReviewId());
        oldReview.setContent(getDefaultIfNull(updReview.getContent(), oldReview.getContent()));
        oldReview.setIsPositive(getDefaultIfNull(updReview.getIsPositive(), oldReview.getIsPositive()));
        oldReview.setUserId(getDefaultIfNull(updReview.getUserId(), oldReview.getUserId()));
        oldReview.setFilmId(getDefaultIfNull(updReview.getFilmId(), oldReview.getFilmId()));

        eventStorage.add(user.getId(), Long.valueOf(oldReview.getFilmId()), EntityType.REVIEW, Operation.UPDATE);

        return ReviewMapper.modelToDto(reviewStorage.update(oldReview));
    }

    @Override
    public void delete(Long reviewId) {
        Review review = getReviewById(reviewId);
        reviewStorage.delete(reviewId);
        eventStorage.add(review.getUserId(), Long.valueOf(review.getFilmId()), EntityType.REVIEW, Operation.REMOVE);
    }

    @Override
    public boolean getUserLike(Long reviewId, Long userId) {
        return reviewStorage.getUserLikeOrDislike(reviewId, userId)
                .orElseThrow(() -> new NotFoundException(String.format(LIKE_OR_DISLIKE_NOT_FOUND, userId, reviewId)));
    }

    private void addLikeOrDislike(Long reviewId, Long userId, boolean isUseful) {
        Review review = getReviewById(reviewId);
        User user = userService.getUserById(userId);
        reviewStorage.addLike(reviewId, userId, isUseful);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {
        addLikeOrDislike(reviewId, userId, true);
        eventStorage.add(userId, reviewId, EntityType.LIKE, Operation.ADD);
        log.info(LIKE_ADDED_MSG, userId, reviewId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        addLikeOrDislike(reviewId, userId, false);
        eventStorage.add(userId, reviewId, EntityType.LIKE, Operation.ADD);
        log.info(DISLIKE_ADDED_MSG, userId, reviewId);
    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {
        Review review = getReviewById(reviewId);
        User user = userService.getUserById(userId);
        boolean isLike = getUserLike(reviewId, userId);
        reviewStorage.deleteLike(reviewId, userId);
        eventStorage.add(userId, reviewId, EntityType.LIKE, Operation.REMOVE);
    }

    @Override
    public void deleteDislike(Long reviewId, Long userId) {
        Review review = getReviewById(reviewId);
        User user = userService.getUserById(userId);
        boolean isLike = getUserLike(reviewId, userId);
        if (!isLike) {
            reviewStorage.deleteLike(reviewId, userId);
        } else {
            throw new NotFoundException(String.format(DISLIKE_NOT_FOUND, userId, reviewId));
        }
        eventStorage.add(userId, reviewId, EntityType.LIKE, Operation.REMOVE);
    }
}
