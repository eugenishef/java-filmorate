package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final List<User> users = new ArrayList<>();
    private String validationBaseMessage = "Валидация при создании пользователя не пройдена: ";

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        String validationErrorMessage = validationBaseMessage;
        if (user.getEmail() == null) {
            validationErrorMessage += "Имейл должен быть указан";

            log.error(validationErrorMessage);
            throw new ValidationException(validationErrorMessage);
        }
        if (!user.getEmail().contains("@")) {
            validationErrorMessage += "Имейл должен содержать символ @";

            log.error(validationErrorMessage);
            throw new ValidationException(validationErrorMessage);
        }
        if (user.getLogin() == null) {
            validationErrorMessage += "Логин не может быть пустым";

            log.error(validationErrorMessage);
            throw new ValidationException(validationErrorMessage);
        }
        if (user.getLogin().contains(" ")) {
            validationErrorMessage += "Логин не должен содержать пробелы";

            log.error(validationErrorMessage);
            throw new ValidationException(validationErrorMessage);
        }
        if (user.getName() == null) {
            log.info("Пользователь не указал имя и данные были автоматически подставлены из поля login");
            user.setName(user.getLogin());
        }
        if (user.getBirthday() != null && user.getBirthday().isAfter(LocalDate.now())) {
            validationErrorMessage += "Дата рождения не может быть в будущем";
            log.error(validationErrorMessage);
            throw new ValidationException(validationErrorMessage);
        }

        user.setId(IdGenerator.getNextId(users, User::getId));
        users.add(user);
        log.info("Добавлен новый пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        String validationErrorMessage = "\"Пользователь с id: \" + user.getId() + \" не найден\"";
        User oldUser = null;
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                oldUser = users.get(i);
                users.set(i, user);
                log.info("Пользовательские данные обновлены. Старые данные: {}, Новые данные: {}", oldUser, user);
                return user;
            }
        }
        log.error(validationErrorMessage);
        throw new IllegalArgumentException(validationErrorMessage);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return users;
    }
}
