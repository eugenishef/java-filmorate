package ru.yandex.practicum.filmorate.validation;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmValidator {
    static final int MAX_FILM_DESCRIPTION_LENGTH = 200;
    static final LocalDate MIN_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    static final String NULL_NAME_ERROR = "Название не может быть пустым";
    static final String NULL_DESCRIPTION_ERROR = "Описание фильма не может быть пустым";
    static final String NULL_RELEASE_DATE_ERROR = "Дата релиза фильма не может быть пустой";
    static final String NULL_DURATION_ERROR = "Продолжительность фильма не может быть пустой";
    static final String DESCRIPTION_LENGTH_ERROR =
            String.format("Превышена максимальная длина описания - %d символов", MAX_FILM_DESCRIPTION_LENGTH);
    static final String RELEASE_DATE_ERROR =
            String.format("Дата релиза не может быть раньше %s", MIN_FILM_RELEASE_DATE.format(DateTimeFormatter.ISO_DATE));
    static final String DURATION_ERROR = "Продолжительность фильма должна быть положительным числом.";

    public static void validate(Film film) {
        validateNull(film);
        validateFormat(film);
    }

    public static void validateNull(Film film) {
        if (Objects.isNull(film.getName()) || film.getName().isBlank()) {
            throwValidationException(NULL_NAME_ERROR);
        }
        if (Objects.isNull(film.getDescription()) || film.getDescription().isBlank()) {
            throwValidationException(NULL_DESCRIPTION_ERROR);
        }
        if (Objects.isNull(film.getReleaseDate())) {
            throwValidationException(NULL_RELEASE_DATE_ERROR);
        }
        if (Objects.isNull(film.getDuration())) {
            throwValidationException(NULL_DURATION_ERROR);
        }
    }

    public static void validateFormat(Film film) {
        if (film.getDescription().length() > MAX_FILM_DESCRIPTION_LENGTH) {
            throwValidationException(DESCRIPTION_LENGTH_ERROR);
        }
        if (film.getReleaseDate().isBefore(MIN_FILM_RELEASE_DATE)) {
            throwValidationException(RELEASE_DATE_ERROR);
        }
        if (film.getDuration() <= 0) {
            throwValidationException(DURATION_ERROR);
        }
    }

    private static void throwValidationException(String message) {
        log.error(message);
        throw new ValidationException(new StringBuilder(message));
    }
}
