package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

public class UserControllerTests {
    @Test
    public void testCreateUserWithValidData() {
        UserController controller = new UserController();
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        assertDoesNotThrow(() -> controller.createUser(user));
        assertEquals(1, controller.getAllUsers().size());
    }

    @Test
    public void testCreateUserWithEmptyEmail() {
        UserController controller = new UserController();
        User user = new User();
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(user));
        assertTrue(exception.getMessage().contains("Имейл должен быть указан"));
    }

    @Test
    public void testCreateUserWithInvalidEmailFormat() {
        UserController controller = new UserController();
        User user = new User();
        user.setEmail("invalid_email");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(user));
        assertTrue(exception.getMessage().contains("Имейл должен содержать символ @"));
    }

    @Test
    public void testCreateUserWithEmptyLogin() {
        UserController controller = new UserController();
        User user = new User();
        user.setEmail("test@example.com");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(user));
        assertTrue(exception.getMessage().contains("Логин не может быть пустым"));
    }

    @Test
    public void testCreateUserWithFutureBirthday() {
        UserController controller = new UserController();
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.now().plusDays(1));

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.createUser(user));
        assertTrue(exception.getMessage().contains("Дата рождения не может быть в будущем"));
    }
}
