package ru.yandex.practicum.filmorate.service.dao;

import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserService {

    Collection<UserDto> findAll();

    UserDto findUserById(Long id);

    User getUserById(Long userId);

    UserDto create(User newUser);

    UserDto update(User updUser);

    void addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    Collection<UserDto> getUserFriends(Long userId);

    Collection<UserDto> getCommonFriends(Long userId, Long otherId);

    Collection<FilmDto> getRecommendations(Long id);

    void deleteUserById(Long id);

    Collection<EventDto> getFeedById(Long id);
}
