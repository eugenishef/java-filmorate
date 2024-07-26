package ru.yandex.practicum.filmorate.validation;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Objects;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class UserValidator {
    static final String NULL_EMAIL_ERROR = "Электронная почта не может быть пустой";
    static final String NULL_LOGIN_ERROR = "Логин не может быть пустым";
    static final String NULL_BIRTHDAY_ERROR = "Дата рождения не должна быть пустой";
    static final String EMAIL_FORMAT_ERROR = "Электронная почта должна содержать символ @";
    static final String LOGIN_FORMAT_ERROR = "Логин не может содержать пробелы";
    static final String BIRTHDAY_FORMAT_ERROR = "Дата рождения не может быть в будущем";

    public static void validate(User user) {
        validateNull(user);
        validateFormat(user);
    }

    public static void validateNull(User user) {
        if (Objects.isNull(user.getEmail()) || user.getEmail().isBlank()) {
            throwValidationException(NULL_EMAIL_ERROR);
        }
        if (Objects.isNull(user.getLogin()) || user.getLogin().isBlank()) {
            throwValidationException(NULL_LOGIN_ERROR);
        }
        if (Objects.isNull(user.getBirthday())) {
            throwValidationException(NULL_BIRTHDAY_ERROR);
        }
    }

    public static void validateFormat(User user) {
        if (user.getEmail() != null && !user.getEmail().contains("@")) {
            throwValidationException(EMAIL_FORMAT_ERROR);
        }
        if (user.getLogin() != null && user.getLogin().contains(" ")) {
            throwValidationException(LOGIN_FORMAT_ERROR);
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            throwValidationException(BIRTHDAY_FORMAT_ERROR);
        }
    }

    private static void throwValidationException(String message) {
        log.error(message);
        throw new ValidationException(new StringBuilder(message));
    }
}
