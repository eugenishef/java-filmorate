package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private List<User> users = new ArrayList<>();
    private int currentId = 1;

    @PostMapping
    public User createUser(@RequestBody User user) {
        user.setId(currentId++);
        users.add(user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == user.getId()) {
                users.set(i, user);
                return user;
            }
        }
        throw new IllegalArgumentException("Пользователь с id: " + user.getId() + " не найден");
    }

    @GetMapping
    public List<User> getAllUsers() {
        return users;
    }
}
