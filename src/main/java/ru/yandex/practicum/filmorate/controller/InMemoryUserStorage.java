package ru.yandex.practicum.filmorate.controller;

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
    private final List<User> users = new ArrayList<>();
    private String validationBaseMessage = "Валидация при создании пользователя не пройдена: ";

    @Override
    public User createUser(@Valid User user) {
        String validationErrorMessage = validationBaseMessage;
        if (user.getEmail() == null) {
            validationErrorMessage += "E-mail должен быть указан";
            throw new ValidationException(validationErrorMessage);
        }
        if (!user.getEmail().contains("@")) {
            validationErrorMessage += "E-mail должен содержать символ @";
            throw new ValidationException(validationErrorMessage);
        }
        if (user.getLogin() == null) {
            validationErrorMessage += "Login(имя пользователя) не может быть пустым";
            throw new ValidationException(validationErrorMessage);
        }
        if (user.getLogin().contains(" ")) {
            validationErrorMessage += "Login(имя пользователя) не должен содержать пробелы";
            throw new ValidationException(validationErrorMessage);
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            validationErrorMessage += "Дата рождения не может быть в будущем";
            throw new ValidationException(validationErrorMessage);
        }

        user.setId(IdGenerator.getNextId(users, User::getId));
        users.add(user);
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
