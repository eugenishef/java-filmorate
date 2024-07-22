/*package ru.yandex.practicum.filmorate.e2e;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@JdbcTest
@AutoConfigureTestDatabase
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRepositoryIntegrationTest {

    @Autowired
    UserRepository userRepository;

    @AfterEach
    public void tearDown() {
        List<User> allUsers = userRepository.getAllUsers();
        allUsers.forEach(user -> userRepository.deleteUser(user.getId()));
    }

    @Test
    public void testCreateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("test_user");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        User createdUser = userRepository.createUser(user);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getId()).isGreaterThan(0);
        assertThat(createdUser.getEmail()).isEqualTo(user.getEmail());

        assertThat(createdUser.getLogin()).isEqualTo(user.getLogin());
    }

    @Test
    public void testGetUserById() {
        User user = new User();
        user.setEmail("test2@example.com");
        user.setLogin("test_user2");
        user.setName("Test User 2");
        user.setBirthday(LocalDate.of(1985, 8, 21));
        User createdUser = userRepository.createUser(user);

        Optional<User> optionalUser = Optional.ofNullable(userRepository.getUserById(createdUser.getId()));

        assertThat(optionalUser).isPresent();
        assertThat(optionalUser.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(optionalUser.get().getLogin()).isEqualTo(user.getLogin());
        assertThat(optionalUser.get().getName()).isEqualTo(user.getName());

        assertThat(optionalUser.get().getBirthday()).isEqualTo(user.getBirthday());
    }

    @Test
    public void testUpdateUser() {
        User user = new User();
        user.setEmail("test3@example.com");
        user.setLogin("test_user3");
        user.setName("Test User 3");
        user.setBirthday(LocalDate.of(1995, 10, 30));
        User createdUser = userRepository.createUser(user);

        createdUser.setName("Updated Test User 3");
        userRepository.updateUser(createdUser);
        Optional<User> optionalUser = Optional.ofNullable(userRepository.getUserById(createdUser.getId()));

        assertThat(optionalUser).isPresent();
        assertThat(optionalUser.get().getName()).isEqualTo("Updated Test User 3");

        assertThat(optionalUser.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(optionalUser.get().getLogin()).isEqualTo(user.getLogin());
        assertThat(optionalUser.get().getBirthday()).isEqualTo(user.getBirthday());
    }
}*/

