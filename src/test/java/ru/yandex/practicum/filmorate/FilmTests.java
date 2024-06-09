package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import ru.yandex.practicum.filmorate.model.Film;

public class FilmTests {

    @Test
    public void testGettersAndSetters() {
        Film film = new Film();
        film.setId(1);
        film.setName("Test Film");
        film.setDescription("Description of the film");
        film.setReleaseDate(LocalDate.of(2020, 10, 15));
        film.setDuration(120);

        assertEquals(1, film.getId());
        assertEquals("Test Film", film.getName());
        assertEquals("Description of the film", film.getDescription());
        assertEquals(LocalDate.of(2020, 10, 15), film.getReleaseDate());
        assertEquals(120, film.getDuration());
    }

    @Test
    public void testToString() {
        Film film = new Film();
        film.setId(1);
        film.setName("Test Film");
        film.setDescription("Description of the film");
        film.setReleaseDate(LocalDate.of(2024, 6, 1));
        film.setDuration(120);

        String expectedToString = "Film{id=1, name='Test Film', description='Description of the film', releaseDate=2024-06-01, duration=120}";
        assertEquals(expectedToString, film.toString());
    }
}
