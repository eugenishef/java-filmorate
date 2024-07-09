package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film addFilm(@Valid Film film);

    Film updateFilm(Film film);

    Optional<Film> getFilmById(int id);

    List<Film> getAllFilms();

    Film getFilmById(long filmId);
}
