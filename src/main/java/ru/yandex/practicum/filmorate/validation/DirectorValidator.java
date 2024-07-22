package ru.yandex.practicum.filmorate.validation;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Objects;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DirectorValidator {
    static final String NULL_NAME_ERROR = "Имя не может быть пустым";

    public static void validate(Director director) {
        validateNull(director);
    }

    public static void validateNull(Director director) {
        if (Objects.isNull(director.getName()) || director.getName().isBlank()) {
            throwValidationException(NULL_NAME_ERROR);
        }
    }

    private static void throwValidationException(String message) {
        log.error(message);
        throw new ValidationException(new StringBuilder(message));
    }
}