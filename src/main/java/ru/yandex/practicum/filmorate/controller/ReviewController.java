package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.dao.ReviewService;

import javax.validation.Valid;

import javax.validation.constraints.Min;
import java.util.Collection;

@RequestMapping(ReviewController.REVIEWS_BASE_PATH)
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequiredArgsConstructor
@Validated
public class ReviewController {
    static final String REVIEWS_BASE_PATH = "/reviews";
    static final String REVIEW_LIKE_PATH = "/{id}/like/{userId}";
    static final String REVIEW_DISLIKE_PATH = "/{id}/dislike/{userId}";

    final ReviewService reviewService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<ReviewDto> findReviews(@RequestParam(required = false) Integer filmId,
                                             @RequestParam(defaultValue = "10") @Min(1) int count) {
        return reviewService.find(filmId, count);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ReviewDto findReviewById(@PathVariable @Min(1) Long id) {
        return reviewService.findReviewById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto create(@Valid @RequestBody Review newReview) {
        return reviewService.create(newReview);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public ReviewDto update(@Valid @RequestBody Review updReview) {
        return reviewService.update(updReview);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable @Min(1) Long id) {
        reviewService.delete(id);
    }

    @PutMapping(REVIEW_LIKE_PATH)
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable @Min(1) Long id, @PathVariable @Min(1) Long userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping(REVIEW_DISLIKE_PATH)
    @ResponseStatus(HttpStatus.OK)
    public void addDislike(@PathVariable @Min(1) Long id, @PathVariable @Min(1) Long userId) {
        reviewService.addDislike(id, userId);
    }


    @DeleteMapping(REVIEW_LIKE_PATH)
    @ResponseStatus(HttpStatus.OK)
    public void deleteLike(@PathVariable @Min(1) Long id, @PathVariable @Min(1) Long userId) {
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping(REVIEW_DISLIKE_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDislike(@PathVariable @Min(1) Long id, @PathVariable @Min(1) Long userId) {
        reviewService.deleteDislike(id, userId);
    }

}
