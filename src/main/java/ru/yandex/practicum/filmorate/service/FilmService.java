package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmService {
    final FilmRepository filmRepository;

    @Autowired
    public FilmService(@Qualifier("filmRepository") FilmRepository filmRepository) {
        this.filmRepository = filmRepository;
    }

    public List<Film> findAll() {
        return filmRepository.getAllFilms();
    }

    public Film addFilm(Film film) {
        filmRepository.createFilm(film);
        return film;
    }

    public Film updateFilm(Film film) {
        filmRepository.updateFilm(film);
        return film;
    }

    public void addLike(long filmId, long userId) {
        Film film = getFilmById(filmId);

        if (film != null && !film.getLikes().contains(userId)) {
            filmRepository.updateFilm(film);
        }
    }

    public void removeLike(long filmId, long userId) {
        Film film = getFilmById(filmId);

        if (film.getLikes().contains(userId)) {
            filmRepository.updateFilm(film);
        }
    }

    public List<Film> topTenPopularFilms(int count) {
        return filmRepository.getAllFilms().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film getFilmById(long filmId) {
        return filmRepository.getAllFilms().stream()
                .filter(film -> film.getId() == filmId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Фильм с id: " + filmId + " не найден"));
    }
}
