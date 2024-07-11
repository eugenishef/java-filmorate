package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserService {
    final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        return userRepository.createUser(user);
    }

    public User updateUser(User user) {
        return userRepository.updateUser(user);
    }

    public List<User> getAllUsers() {
        return userRepository.getAllUsers();
    }

    public void addFriend(int userId, int friendId) {
        User user = userRepository.getUserById(userId);
        User friend = userRepository.getUserById(friendId);

        if (user != null && friend != null) {
            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
            userRepository.updateUser(user);
            userRepository.updateUser(friend);
        }
    }

    public void removeFriend(int userId, int friendId) {
        User user = userRepository.getUserById(userId);
        User friend = userRepository.getUserById(friendId);

        if (user != null && friend != null) {
            user.getFriends().remove(friendId);
            friend.getFriends().remove(userId);
            userRepository.updateUser(user);
            userRepository.updateUser(friend);
        }
    }

    public List<User> getUserFriends(int userId) {
        User user = userRepository.getUserById(userId);

        if (user == null) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }

        List<User> friends = new ArrayList<>();
        for (Integer friendId : user.getFriends()) {
            User friend = userRepository.getUserById(friendId);
            if (friend != null) {
                friends.add(friend);
            }
        }
        return friends;
    }

    public List<User> getCommonFriends(int userId, int otherUserId) {
        User user = userRepository.getUserById(userId);
        User otherUser = userRepository.getUserById(otherUserId);
        List<User> commonFriends = new ArrayList<>();

        if (user != null && otherUser != null) {
            for (Integer friendId : user.getFriends()) {
                if (otherUser.getFriends().contains(friendId)) {
                    User commonFriend = userRepository.getUserById(friendId);
                    if (commonFriend != null) {
                        commonFriends.add(commonFriend);
                    }
                }
            }
        }
        return commonFriends;
    }

    public User getUserById(long userId) {
        return userRepository.getAllUsers().stream()
                .filter(user -> user.getId() == userId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден"));
    }
}