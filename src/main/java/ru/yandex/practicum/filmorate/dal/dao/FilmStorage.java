package ru.yandex.practicum.filmorate.dal.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Collection<Film> findAll();

    Film create(Film newFilm);

    Film update(Film updFilm);

    Optional<Film> findFilmById(Integer id);

    void addLike(Integer filmId, Long userId);

    void deleteLike(Integer filmId, Long userId);

    Collection<Film> findCommonFilms(Long userId, Long friendId);

    Collection<Film> listFilmsDirector(int directorId);

    Collection<Long> findFilmLikesByFilmId(Integer filmId);

    List<Film> searchFilms(String query, String by);

    void deleteFilm(Integer filmId);
}
