package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId);

        if (film != null && !film.getLikes().contains(userId)) {
            film.getLikes().add(userId);
        }
    }

    public void removeLike(long filmId, long userId) {
        Film film = filmStorage.getFilmById(filmId);

        if (film != null) {
            film.getLikes().remove(userId);
        }
    }

    public List<Film> topTenPopularFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilmById(long filmId) {
        return filmStorage.getAllFilms().stream()
                .filter(film -> film.getId() == filmId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + filmId + " не найден"));
    }
}
