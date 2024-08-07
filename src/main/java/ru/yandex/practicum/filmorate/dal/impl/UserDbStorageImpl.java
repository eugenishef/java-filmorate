package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dal.dao.UserStorage;
import ru.yandex.practicum.filmorate.dal.extractors.UserExtractor;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class UserDbStorageImpl implements UserStorage {
    final JdbcTemplate jdbc;
    final UserRowMapper userRowMapper;
    final UserExtractor userExtractor;

    @Override
    public Collection<User> findAll() {
        String query = "SELECT u.*, uf.user_friend_id FROM users u " +
                "LEFT JOIN user_friends uf ON u.id = uf.user_id";
        return jdbc.query(query, userExtractor);
    }

    public Set<User> getFriendsByUserId(Long userId) {
        String query = "SELECT u.*, uf2.user_friend_id FROM users u " +
                "JOIN user_friends uf ON u.id = uf.user_friend_id " +
                "LEFT JOIN user_friends uf2 ON u.id = uf2.user_id " +
                "WHERE uf.user_id = ?";
        return new HashSet<>(Objects.requireNonNull(jdbc.query(query, userExtractor, userId)));
    }

    @Override
    public User create(User newUser) {
        UserValidator.validateNull(newUser);
        UserValidator.validateFormat(newUser);
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO users(email, login, name, birthday)" +
                "VALUES (?, ?, ?, ?)";
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, newUser.getEmail());
            ps.setObject(2, newUser.getLogin());
            ps.setObject(3, newUser.getName());
            ps.setObject(4, newUser.getBirthday());
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKeyAs(Long.class);
        if (id != null) {
            log.info("Пользователь {} добавлен", newUser);
            return User.builder()
                    .id(id)
                    .email(newUser.getEmail())
                    .login(newUser.getLogin())
                    .name(newUser.getName())
                    .birthday(newUser.getBirthday())
                    .build();
        } else {
            throw new RuntimeException("Не удалось сохранить данные");
        }
    }

    @Override
    public User update(User updUser) {
        String query = "UPDATE users " +
                "SET email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE id = ?";
        int rowsUpdated = jdbc.update(query,
                updUser.getEmail(),
                updUser.getLogin(),
                updUser.getName(),
                updUser.getBirthday(),
                updUser.getId());
        if (rowsUpdated == 0) {
            throw new RuntimeException("Не удалось обновить данные");
        } else {
            log.info("Пользователь с id = {} обновлен", updUser.getId());
            return updUser;
        }
    }

    @Override
    public Optional<User> findUserById(Long id) {
        String query = "SELECT * FROM users WHERE id = ?";
        List<User> result = jdbc.query(query, userRowMapper, id);
        if (result.size() == 0) {
            return Optional.empty();
        }
        User user = result.getFirst();
        Set<User> friends = new HashSet<>(getFriendsByUserId(user.getId()));
        user.setFriends(friends.stream().map(User::getId).collect(Collectors.toSet()));
        return Optional.of(user);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String query =
                "MERGE INTO user_friends (user_id, user_friend_id) KEY (user_id, user_friend_id) VALUES (?, ?)";
        int rowsMerged = jdbc.update(query, userId, friendId);
        if (rowsMerged > 0) {
            log.info("Пользователь с Id = {} стал другом пользователя с Id = {}", userId,
                    friendId);
        }
    }

    @Override
    public boolean deleteFriend(Long userId, Long friendId) {
        String query = "DELETE FROM user_friends WHERE user_id = ? AND user_friend_id = ?";
        int rowsDeleted = jdbc.update(query, userId, friendId);
        return (rowsDeleted > 0);
    }

    @Override
    public Collection<User> getCommonFriends(Long userId, Long otherId) {
        String query = "SELECT u.*, uf.user_friend_id " +
                "FROM user_friends uf1 " +
                "JOIN user_friends uf2 ON uf1.user_friend_id = uf2.user_friend_id " +
                "JOIN users u ON u.id = uf1.user_friend_id " +
                "LEFT JOIN user_friends uf ON u.id = uf.user_id " +
                "WHERE uf1.user_id = ? AND uf2.user_id = ?";
        return jdbc.query(query, userExtractor, userId, otherId);
    }

    public Set<Integer> findUsersFilms(Long userId) {
        return new HashSet<>(jdbc.queryForList("SELECT fu.film_id FROM film_userlikes fu " +
                "WHERE fu.user_id = ? ", Integer.class, userId));
    }

    @Override
    public void deleteUser(Long id) {
        String query = "DELETE FROM user_friends WHERE user_id = ? OR user_friend_id = ?;" +
                "DELETE FROM film_userlikes WHERE user_id = ?;" +
                "DELETE FROM review WHERE user_id = ?;" +
                "DELETE FROM review_userlikes WHERE user_id = ?;" +
                "DELETE FROM users WHERE id = ?";
        jdbc.update(query, id, id, id, id, id, id);
    }

}
