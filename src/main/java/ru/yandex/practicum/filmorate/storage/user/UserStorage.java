package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    User addUser(User user);

    User createUser(@Valid @RequestBody User user);

    User updateUser(@RequestBody User user);

    Optional<User> getUserById(int id);

    List<User> getAllUsers();

    User getUserById(long id);
}