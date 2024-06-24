package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

public interface UserStorage {
    User createUser(@Valid @RequestBody User user);

    User updateUser(@RequestBody User user);

    List<User> getAllUsers();

    User getUserById(long id);
}