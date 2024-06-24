package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

public interface FilmStorage {
    Film addFilm(@Valid Film film);

    Film updateFilm(Film film);

    List<Film> getAllFilms();

    Film getFilmById(long filmId);
}
