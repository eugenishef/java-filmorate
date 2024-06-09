package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import ru.yandex.practicum.filmorate.model.User;

public class UserTests {
    @Test
    public void testGettersAndSetters() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        assertEquals(1, user.getId());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("testuser", user.getLogin());
        assertEquals("Test User", user.getName());
        assertEquals(LocalDate.of(1990, 5, 15), user.getBirthday());
    }

    @Test
    public void testToString() {
        User user = new User();
        user.setId(1);
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 5, 15));

        String expectedToString = "User{id=1, email='test@example.com', login='testuser', name='Test User', birthday=1990-05-15}";
        assertEquals(expectedToString, user.toString());
    }
}
