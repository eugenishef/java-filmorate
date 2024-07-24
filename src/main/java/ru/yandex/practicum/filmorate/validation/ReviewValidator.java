package ru.yandex.practicum.filmorate.validation;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.util.Objects;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewValidator {
    static final String NULL_USERID_ERROR = "Пользователь должен быть указан";
    static final String NULL_FILMID_ERROR = "Фильм должен быть указан";

    public static void validateNull(Review review) {
        if (Objects.isNull(review.getUserId())) {
            throwValidationException(NULL_USERID_ERROR);
        }
        if (Objects.isNull(review.getFilmId())) {
            throwValidationException(NULL_FILMID_ERROR);
        }
    }

    private static void throwValidationException(String message) {
        log.error(message);
        throw new ValidationException(new StringBuilder(message));
    }
}
