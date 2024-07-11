package ru.yandex.practicum.filmorate.dal;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;

import java.util.List;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRepository {
    final JdbcTemplate jdbc;
    final RowMapper<User> userRowMapper;

    public UserRepository(JdbcTemplate jdbc, UserRowMapper userRowMapper) {
        this.jdbc = jdbc;
        this.userRowMapper = userRowMapper;
    }

    public User createUser(User user) {
        String sql = "INSERT INTO Users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        jdbc.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        return user;
    }

    public User updateUser(User user) {
        String sql = "UPDATE Users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbc.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    public List<User> getAllUsers() {
        String sql = "SELECT * FROM Users";
        return jdbc.query(sql, userRowMapper);
    }

    public User getUserById(long id) {
        String sql = "SELECT * FROM Users WHERE id = ?";
        return jdbc.queryForObject(sql, userRowMapper, id);
    }

    public void deleteUser(long id) {
        String sql = "DELETE FROM Users WHERE id = ?";
        jdbc.update(sql, id);
    }

    public void addFriend(int userId, int friendId) {
        String sql = "INSERT INTO Friendships (user_id, friend_id, status) VALUES (?, ?, 'CONFIRMED')";
        jdbc.update(sql, userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        String sql = "DELETE FROM Friendships WHERE user_id = ? AND friend_id = ?";
        jdbc.update(sql, userId, friendId);
    }

    public List<User> getUserFriends(int userId) {
        String sql = "SELECT u.* FROM Users u JOIN Friendships f ON u.id = f.friend_id WHERE f.user_id = ?";
        return jdbc.query(sql, userRowMapper, userId);
    }

    public List<User> getCommonFriends(int userId1, int userId2) {
        String sql = "SELECT u.* FROM Users u " +
                "JOIN Friendships f1 ON u.id = f1.friend_id " +
                "JOIN Friendships f2 ON u.id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbc.query(sql, userRowMapper, userId1, userId2);
    }
}
