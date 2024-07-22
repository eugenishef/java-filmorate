package ru.yandex.practicum.filmorate.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.dao.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.dao.FilmService;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    final FilmStorage filmStorage;
    final UserStorage userStorage;
    final RatingStorage ratingStorage;
    final GenreStorage genreStorage;
    final DirectorStorage directorStorage;

    static final String FILM_NOT_FOUND_MSG = "Фильм с id = %d не найден";
    static final String RATING_NOT_FOUND_MSG = "Рейтинг фильма с id = %d не найден";
    static final String GENRE_NOT_FOUND_MSG = "Жанр фильма с id = %d не найден";
    static final String USER_NOT_FOUND_MSG = "Пользователь с id = %d не найден";
    static final String LIKE_ADDED_MSG = "Добавлен лайк пользователя c id = {} к фильму с filmId = {}";
    static final String LIKE_REMOVED_MSG = "Удален лайк пользователя c id = {} к фильму с filmId = {}";
    static final String TOP_FILMS_MSG = "Список {} наиболее популярных фильмов для вывода: {}";
    static final String DIRECTOR_FILMS_MSG = "Список фильмов режиссера {} отсортированных по {}: {}";

    private <T> T getDefaultIfNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    private void validateGenres(Collection<Genre> genres) {
        if (genres != null) {
            Collection<Integer> genreIds = genreStorage.findAll().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());

            for (Genre genre : genres) {
                if (!genreIds.contains(genre.getId())) {
                    throw new ValidationException(new StringBuilder(String.format(GENRE_NOT_FOUND_MSG, genre.getId())));
                }
            }
        }
    }

    @Override
    public Collection<FilmDto> findAll() {
        Collection<Film> allFilms = filmStorage.findAll();
        return allFilms.stream().map(FilmMapper::modelToDto).toList();
    }

    @Override
    public FilmDto findFilmById(Integer id) {
        return FilmMapper.modelToDto(
                filmStorage.findFilmById(id)
                        .orElseThrow(() -> new NotFoundException(String.format(FILM_NOT_FOUND_MSG, id)))
        );
    }

    @Override
    public FilmDto create(Film newFilm) {
        FilmValidator.validateNull(newFilm);
        FilmValidator.validateFormat(newFilm);

        Integer ratingId = newFilm.getMpa().getId();
        ratingStorage.findRatingById(ratingId)
                .orElseThrow(() -> new ValidationException(new StringBuilder(String.format(RATING_NOT_FOUND_MSG, ratingId))));

        validateGenres(newFilm.getGenres());

        Film film = filmStorage.create(newFilm);
        return FilmMapper.modelToDto(film);
    }

    @Override
    public FilmDto update(Film updFilm) {
        FilmValidator.validateFormat(updFilm);
        Film oldFilm = filmStorage.findFilmById(updFilm.getId())
                .orElseThrow(() -> new NotFoundException(String.format(FILM_NOT_FOUND_MSG, updFilm.getId())));

        oldFilm.setDescription(getDefaultIfNull(updFilm.getDescription(), oldFilm.getDescription()));
        oldFilm.setName(getDefaultIfNull(updFilm.getName(), oldFilm.getName()));
        oldFilm.setReleaseDate(getDefaultIfNull(updFilm.getReleaseDate(), oldFilm.getReleaseDate()));
        oldFilm.setDuration(getDefaultIfNull(updFilm.getDuration(), oldFilm.getDuration()));
        oldFilm.setMpa(getDefaultIfNull(updFilm.getMpa(), oldFilm.getMpa()));
        oldFilm.setGenres(getDefaultIfNull(updFilm.getGenres(), oldFilm.getGenres()));

        return FilmMapper.modelToDto(filmStorage.update(updFilm));
    }

    @Override
    public void addLike(Integer filmId, Long userId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException(String.format(FILM_NOT_FOUND_MSG, filmId)));
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));
        filmStorage.addLike(filmId, userId);

        log.info(LIKE_ADDED_MSG, userId, filmId);
    }

    @Override
    public void deleteLike(Integer filmId, Long userId) {
        Film film = filmStorage.findFilmById(filmId)
                .orElseThrow(() -> new NotFoundException(String.format(FILM_NOT_FOUND_MSG, filmId)));
        User user = userStorage.findUserById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(USER_NOT_FOUND_MSG, userId)));
        filmStorage.deleteLike(filmId, userId);

        log.info(LIKE_REMOVED_MSG, userId, filmId);
    }

    @Override
    public Collection<FilmDto> getPopularFilms(Integer count, Integer genreId, Integer year) {
        Collection<Film> topPopularFilms = filmStorage.findAll();

        if (count == null) {
            count = topPopularFilms.size();
        }

        Collection<FilmDto> topPopularFilmsDto = topPopularFilms.stream()
                .map(FilmMapper::modelToDto)
                .filter(filmDto -> {
                    if (year == null) {
                        return true;
                    } else {
                        return filmDto.getReleaseDate().getYear() == year;
                    }
                })
                .filter(filmDto -> {
                    if (genreId == null) {
                        return true;
                    } else {
                        GenreMapper genreMapper = null;
                        GenreDto currentGenreDto = genreMapper.modelToDto(genreStorage.findGenreById(genreId).get());
                        return filmDto.getGenres().contains(currentGenreDto);
                    }
                })
                .sorted((f1, f2) -> Long.compare(f2.getUserLikes().size(), f1.getUserLikes().size()))
                .limit(count)
                .toList();
        log.debug(TOP_FILMS_MSG, count, topPopularFilms);

        return topPopularFilmsDto;

    }

    @Override
    public Collection<FilmDto> listFilmsDirector(int directorId, String sortBy) {
        directorStorage.findDirectorById(directorId);
        Collection<FilmDto> listFilms;
        switch (sortBy) {
            case "likes":
                Comparator<FilmDto> byLikes = Comparator.comparingInt(f -> f.getUserLikes().size());
                listFilms = filmStorage.listFilmsDirector(directorId).stream()
                        .map(FilmMapper::modelToDto)
                        .sorted(byLikes.reversed())
                        .toList();
                break;
            case "year":
                Comparator<FilmDto> byDate = Comparator.comparing(FilmDto::getReleaseDate);
                listFilms = filmStorage.listFilmsDirector(directorId).stream()
                        .map(FilmMapper::modelToDto)
                        .sorted(byDate)
                        .toList();
                break;
            default:
                throw new ValidationException(new StringBuilder("неверный параметр сортировки"));
        }
        log.debug(DIRECTOR_FILMS_MSG, directorId, sortBy, listFilms);
        return listFilms;
    }
}
