package ru.yandex.practicum.filmorate.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.dao.EventStorage;
import ru.yandex.practicum.filmorate.dal.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.EntityType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.dao.UserService;
import ru.yandex.practicum.filmorate.dal.dao.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.*;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final UserStorage userStorage;
    final FilmStorage filmStorage;
    final EventStorage eventStorage;

    static final String USER_NOT_FOUND_MSG = "Пользователь с id = %d не найден";
    static final String FRIEND_NOT_FOUND_MSG = "Пользователь для %s с id = %d не найден";
    static final String ALREADY_FRIEND_MSG = "Пользователь с id = %d уже является другом пользователя c id = %d";
    static final String SELF_FRIENDSHIP_MSG = "Пользователь не может добавить себя в друзья. Для id пользователя и id друга передано одинаковое значение %d";
    static final String FRIEND_ADDED_LOG_MSG = "Пользователь c friendId = {} добавлен в список друзей пользователя с id = {}";
    static final String FRIEND_DELETED_LOG_MSG = "Пользователь c friendId = {} удален из списка друзей пользователя с id = {}";
    static final String FRIENDS_LIST_LOG_MSG = "Список друзей пользователя {} для вывода: {}";
    static final String COMMON_FRIENDS_LIST_LOG_MSG = "Список общих друзей пользователей {} и {} для вывода: {}";
    static final String EQUAL_IDS_LOG_MSG = "Пользователь не может добавить себя в друзья. Для id пользователя и id друга передано одинаковое значение {}";
    static final String FRIENDSHIP_EXISTS_LOG_MSG = "Пользователь с id = {} уже является другом пользователя c id = {}";

    @Override
    public User getUserById(Long userId) {
        return userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));
    }

    @Override
    public Collection<UserDto> findAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::modelToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findUserById(Long id) {
        return UserMapper.modelToDto(getUserById(id));
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        User user = getUserById(userId);
        User friend = getUserById(friendId);

        if (userId.equals(friendId)) {
            String equalIds = String.format(SELF_FRIENDSHIP_MSG, userId);
            log.warn(EQUAL_IDS_LOG_MSG, userId);
            throw new ValidationException(new StringBuilder(equalIds));
        }

        if (userStorage.getFriendsByUserId(userId).contains(friendId)) {
            String friendshipExists = String.format(ALREADY_FRIEND_MSG, userId, friendId);
            log.warn(FRIENDSHIP_EXISTS_LOG_MSG, friendshipExists);
            throw new ValidationException(new StringBuilder(friendshipExists));
        }

        userStorage.addFriend(userId, friendId);
        eventStorage.add(userId, friendId, EntityType.FRIEND, Operation.ADD);
        log.info(FRIEND_ADDED_LOG_MSG, friendId, userId);
    }

    @Override
    public void deleteFriend(Long userId, Long friendId) {
        getUserById(userId);
        getUserById(friendId);

        if (userId.equals(friendId)) {
            String equalIds = String.format(SELF_FRIENDSHIP_MSG, userId);
            log.warn(EQUAL_IDS_LOG_MSG, equalIds);
            throw new ValidationException(new StringBuilder(equalIds));
        }

        if (userStorage.deleteFriend(userId, friendId)) {
            log.info(FRIEND_DELETED_LOG_MSG, friendId, userId);
        }
        eventStorage.add(userId, friendId, EntityType.FRIEND, Operation.REMOVE);
    }

    @Override
    public Collection<UserDto> getUserFriends(Long userId) {
        User user = getUserById(userId);
        Collection<UserDto> userFriends = userStorage.getFriendsByUserId(userId).stream()
                .map(UserMapper::modelToDto)
                .collect(Collectors.toSet());
        log.debug(FRIENDS_LIST_LOG_MSG, userId, userFriends);
        return userFriends;
    }

    @Override
    public Collection<UserDto> getCommonFriends(Long userId, Long otherId) {
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(FRIEND_NOT_FOUND_MSG, userId)));
        User other = userStorage.findUserById(otherId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с id = %d не найден", otherId)));
        Collection<UserDto> commonFriends = userStorage.getCommonFriends(userId, otherId).stream()
                .map(UserMapper::modelToDto)
                .collect(Collectors.toList());
        log.debug(COMMON_FRIENDS_LIST_LOG_MSG, userId, otherId, commonFriends);
        return commonFriends;
    }

    @Override
    public UserDto create(User newUser) {
        return UserMapper.modelToDto(userStorage.create(newUser));
    }

    @Override
    public UserDto update(User updUser) {
        UserValidator.validateFormat(updUser);
        User oldUser = userStorage.findUserById(updUser.getId())
                .orElseThrow(() -> {
                    String idNotFound = String.format(USER_NOT_FOUND_MSG, updUser.getId());
                    log.warn(USER_NOT_FOUND_MSG, idNotFound);
                    return new NotFoundException(idNotFound);
                });

        oldUser.setLogin(Optional.ofNullable(updUser.getLogin()).filter(login -> !login.isBlank()).orElse(oldUser.getLogin()));
        oldUser.setEmail(Optional.ofNullable(updUser.getEmail()).filter(email -> !email.isBlank()).orElse(oldUser.getEmail()));
        oldUser.setBirthday(Optional.ofNullable(updUser.getBirthday()).orElse(oldUser.getBirthday()));
        oldUser.setName(Optional.ofNullable(updUser.getName()).filter(name -> !name.isBlank()).orElse(oldUser.getName()));

        return UserMapper.modelToDto(userStorage.update(oldUser));
    }

    @Override
    public void deleteUserById(Long id) {
        getUserById(id);
        userStorage.deleteUser(id);
    }

    @Override
    public Collection<FilmDto> getRecommendations(Long id) {
        Set<Integer> usersFilms = userStorage.findUsersFilms(id);
        List<Long> collaborators = new ArrayList<>();
        Collection<FilmDto> recommendedFilms = new ArrayList<>();
        int maxFreq = 0;
        Long idMaxFreq = null;

        for (Integer filmId : usersFilms) {
            collaborators.addAll(filmStorage.findFilmLikesByFilmId(filmId));
        }

        for (Long userId : collaborators) {
            int freq = (int) collaborators
                    .stream()
                    .filter(currentId -> currentId.equals(userId) && !currentId.equals(id))
                    .count();

            if (freq > maxFreq) {
                maxFreq = freq;
                idMaxFreq = userId;
            }
        }
        Set<Integer> collaboratorsFilms = new HashSet<>(userStorage.findUsersFilms(idMaxFreq));

        for (Integer collabFilmId : collaboratorsFilms) {
            if (!usersFilms.contains(collabFilmId)) {
                Film recommendedFilm = filmStorage.findFilmById(collabFilmId).get();
                recommendedFilms.add(FilmMapper.modelToDto(recommendedFilm));
            }
        }
        return recommendedFilms;
    }

    @Override
    public Collection<EventDto> getFeedById(Long id) {
        getUserById(id);
        return eventStorage.findById(id).stream()
                .map(EventMapper::modelToDto)
                .collect(Collectors.toList());

    }
}
