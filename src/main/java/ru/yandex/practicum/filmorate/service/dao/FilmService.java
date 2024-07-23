package ru.yandex.practicum.filmorate.service.dao;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {

    Collection<FilmDto> findAll();

    FilmDto findFilmById(Integer id);

    FilmDto create(Film newFilm);

    FilmDto update(Film updFilm);

    void addLike(Integer filmId, Long userId);

    void deleteLike(Integer filmId, Long userId);

    Collection<FilmDto> getTopPopularFilms(int count);

    Collection<FilmDto> getCommonFilms(Long userId, Long friendId);

    Collection<FilmDto> listFilmsDirector(int directorId, String param);

    List<FilmDto> searchFilms(String query, String by);
}
