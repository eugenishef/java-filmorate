package ru.yandex.practicum.filmorate.exception;

public class ValidationException extends RuntimeException {
    public ValidationException(StringBuilder message) {
        super(String.valueOf(message));
    }
}
