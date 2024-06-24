package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (user != null && friend != null) {
            user.getFriends()
                    .add(friendId);
            friend.getFriends()
                    .add(userId);
        }
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        if (user != null && friend != null) {
            user.getFriends()
                    .remove(friendId);
            friend.getFriends()
                    .remove(userId);
        }
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);
        List<User> commonFriends = new ArrayList<>();

        if (user != null && otherUser != null) {
            for (Integer friendId : user.getFriends()) {
                if (otherUser.getFriends().contains(friendId)) {
                    commonFriends.add(userStorage.getUserById(friendId));
                }
            }
        }
        return commonFriends;
    }

    public User getUserById(long userId) {
        return userStorage.getAllUsers().stream()
                .filter(user -> user.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
    }
}
