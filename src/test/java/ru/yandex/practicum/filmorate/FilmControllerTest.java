package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

public class FilmControllerTest {

    @Test
    public void testAddFilmWithValidData() {
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description of the film");
        film.setReleaseDate(LocalDate.of(2020, 10, 15));
        film.setDuration(120);

        assertDoesNotThrow(() -> controller.addFilm(film));
        assertEquals(1, controller.getAllFilms().size());
    }

    @Test
    public void testAddFilmWithEmptyName() {
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setDescription("Description of the film");
        film.setReleaseDate(LocalDate.of(2020, 10, 15));
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(film));
        assertTrue(exception.getMessage().contains("Название не может быть пустым"));
    }

    @Test
    public void testAddFilmWithLongDescription() {
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("This is a very long description of the film. This description exceeds the maximum length allowed for film description. This is a very long description of the film. This description exceeds the maximum length allowed for film description.");
        film.setReleaseDate(LocalDate.of(2020, 10, 15));
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(film));
        assertTrue(exception.getMessage().contains("Максимальная длина описания — 200 символов"));
    }

    @Test
    public void testAddFilmWithInvalidReleaseDate() {
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description of the film");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(120);

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(film));
        assertTrue(exception.getMessage().contains("Дата релиза — не раньше 28 декабря 1895 года"));
    }

    @Test
    public void testAddFilmWithNegativeDuration() {
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description of the film");
        film.setReleaseDate(LocalDate.of(2020, 10, 15));
        film.setDuration(-120);

        ValidationException exception = assertThrows(ValidationException.class, () -> controller.addFilm(film));
        assertTrue(exception.getMessage().contains("Продолжительность фильма должна быть положительным числом"));
    }

    @Test
    public void testUpdateNonExistingFilm() {
        FilmController controller = new FilmController();
        Film film = new Film();
        film.setId(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> controller.updateFilm(film));
        assertTrue(exception.getMessage().contains("Фильм с id: 1 не найден"));
    }
}
