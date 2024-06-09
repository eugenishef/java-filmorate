package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

public class UserControllerTests {
    private UserController controller;

    @BeforeEach
    public void setUp() {
        controller = new UserController();
    }

    private User createUser(String email, String login, String name, LocalDate birthday) {
        User user = new User();
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);
        return user;
    }

    @Test
    public void testCreateUserWithValidData() {
        User user = createUser("test@example.com", "testuser", "Test User", LocalDate.of(1990, 5, 15));

        assertDoesNotThrow(() -> controller.createUser(user));
        assertEquals(1, controller.getAllUsers().size());
    }

    @Test
    public void testCreateUserWithEmptyEmail() {
        User user = createUser(null, "testuser", "Test User", LocalDate.of(1990, 5, 15));

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(user));
        assertTrue(exception.getMessage().contains("E-mail должен быть указан"));
    }

    @Test
    public void testCreateUserWithInvalidEmailFormat() {
        User user = createUser("invalid_email", "testuser", "Test User", LocalDate.of(1990, 5, 15));

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(user));
        assertTrue(exception.getMessage().contains("E-mail должен содержать символ @"));
    }

    @Test
    public void testCreateUserWithEmptyLogin() {
        User user = createUser("test@example.com", null, "Test User", LocalDate.of(1990, 5, 15));

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(user));
        assertTrue(exception.getMessage().contains("Login(имя пользователя) не может быть пустым"));
    }

    @Test
    public void testCreateUserWithFutureBirthday() {
        User user = createUser("test@example.com", "testuser", "Test User", LocalDate.now().plusDays(1));

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(user));
        assertTrue(exception.getMessage().contains("Дата рождения не может быть в будущем"));
    }
}
