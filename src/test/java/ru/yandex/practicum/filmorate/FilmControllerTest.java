package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

public class FilmControllerTest {
    private FilmController controller;

    @BeforeEach
    public void setUp() {
        controller = new FilmController();
    }

    private Film createFilm(String name, String description, LocalDate releaseDate, int duration) {
        Film film = new Film();
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        return film;
    }

    @Test
    public void testAddFilmWithValidData() {
        Film film = createFilm("Test Film", "Description of the film", LocalDate.of(2020, 10, 15), 120);

        assertDoesNotThrow(() -> controller.addFilm(film));
        assertEquals(1, controller.getAllFilms().size());
    }

    @Test
    public void testAddFilmWithEmptyName() {
        Film film = createFilm(null, "Description of the film", LocalDate.of(2020, 10, 15), 120);

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(film));
        assertTrue(exception.getMessage().contains("Название фильма не может быть пустым"));
    }

    @Test
    public void testAddFilmWithLongDescription() {
        Film film = createFilm("Test Film", "This is a very long description of the film. This description exceeds the maximum length allowed for film description. This is a very long description of the film. This description exceeds the maximum length allowed for film description.", LocalDate.of(2020, 10, 15), 120);

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(film));
        assertTrue(exception.getMessage().contains("Максимальная длина описания — 200 символов"));
    }

    @Test
    public void testAddFilmWithInvalidReleaseDate() {
        Film film = createFilm("Test Film", "Description of the film", LocalDate.of(1800, 1, 1), 120);

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(film));
        assertTrue(exception.getMessage().contains("Дата релиза — не раньше 28 декабря 1895 года"));
    }

    @Test
    public void testAddFilmWithNegativeDuration() {
        Film film = createFilm("Test Film", "Description of the film", LocalDate.of(2020, 10, 15), -120);

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(film));
        assertTrue(exception.getMessage().contains("Продолжительность фильма должна быть положительным числом"));
    }

    @Test
    public void testUpdateNonExistingFilm() {
        Film film = createFilm("Test Film", "Description of the film", LocalDate.of(2020, 10, 15), 120);
        film.setId(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> controller.updateFilm(film));
        assertTrue(exception.getMessage().contains("Фильм с id: 1 не найден"));
    }
}
