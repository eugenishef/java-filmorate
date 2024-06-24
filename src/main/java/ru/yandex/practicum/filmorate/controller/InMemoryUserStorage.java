package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);
    private final List<User> users = new ArrayList<>();
    private String validationBaseMessage = "Валидация при создании пользователя не пройдена: ";

    @Override
    public User createUser(@Valid User user) {
        StringBuilder validationErrorMessage = new StringBuilder(validationBaseMessage);
        boolean isValid = true;

        if (user.getEmail() == null) {
            validationErrorMessage.append("E-mail должен быть указан");
            isValid = false;

            log.error(String.valueOf(validationErrorMessage));
            throw new ValidationException(validationErrorMessage);
        }
        if (user.getEmail() != null && !user.getEmail().contains("@")) {
            validationErrorMessage.append("E-mail должен содержать символ @; ");
            isValid = false;

            log.error(String.valueOf(validationErrorMessage));
            throw new ValidationException(validationErrorMessage);
        }
        if (user.getLogin() == null) {
            validationErrorMessage.append("Login(имя пользователя) не может быть пустым; ");
            isValid = false;

            log.error(String.valueOf(validationErrorMessage));
            throw new ValidationException(validationErrorMessage);
        }
        if (user.getLogin() != null && user.getLogin().contains(" ")) {
            validationErrorMessage.append("Login(имя пользователя) не должен содержать пробелы; ");
            isValid = false;

            log.error(String.valueOf(validationErrorMessage));
            throw new ValidationException(validationErrorMessage);
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            validationErrorMessage.append("Дата рождения не может быть в будущем; ");
            isValid = false;

            log.error(String.valueOf(validationErrorMessage));
            throw new ValidationException(validationErrorMessage);
        }

        if (!isValid) {
            throw new ValidationException(new StringBuilder(validationErrorMessage.toString()));
        }

        user.setId(IdGenerator.getNextId(users, User::getId));
        users.add(user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        String validationErrorMessage = "Пользователь с id: " + user.getId() + " не найден";
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.set(i, user);
                return user;
            }
        }
        throw new IllegalArgumentException(validationErrorMessage);
    }

    @Override
    public List<User> getAllUsers() {
        return users;
    }

    @Override
    public User getUserById(long id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst()
                .orElse(null);
    }
}
