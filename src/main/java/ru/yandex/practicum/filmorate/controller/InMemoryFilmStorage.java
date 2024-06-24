package ru.yandex.practicum.filmorate.controller;

import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private List<Film> films = new ArrayList<>();
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private String validationBaseMessage = "Валидация при добавлении фильма не пройдена: ";

    @Override
    public Film addFilm(@Valid Film film) {
        StringBuilder validationErrorMessage = new StringBuilder(validationBaseMessage);
        boolean isValid = true;

        if (film.getName() == null || film.getName().length() == 0) {
            validationErrorMessage.append("Название фильма не может быть пустым");
            isValid = false;

            log.error(String.valueOf(validationErrorMessage));
            throw new ValidationException(validationErrorMessage);

        }
        if (film.getDescription().length() > 200) {
            validationErrorMessage.append("Максимальная длина описания — 200 символов");
            isValid = false;

            log.error(String.valueOf(validationErrorMessage));
            throw new ValidationException(validationErrorMessage);
        }
        if (film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            validationErrorMessage.append("Дата релиза — не раньше 28 декабря 1895 года");
            isValid = false;

            log.error(String.valueOf(validationErrorMessage));
            throw new ValidationException(validationErrorMessage);
        }
        if (film.getDuration() <= 0) {
            validationErrorMessage.append("Продолжительность фильма должна быть положительным числом");
            isValid = false;

            log.error(String.valueOf(validationErrorMessage));
            throw new ValidationException(validationErrorMessage);
        }

        if (!isValid) {
            throw new ValidationException(new StringBuilder(validationErrorMessage.toString()));
        }

        film.setId(IdGenerator.getNextId(films, Film::getId));
        films.add(film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == film.getId()) {
                films.set(i, film);
                log.info("Фильм c id {} успешно обновлен", film.getId());
                return film;
            }
        }
        throw new IllegalArgumentException("Фильм с id: " + film.getId() + " не найден");
    }

    @Override
    public List<Film> getAllFilms() {
        return films;
    }

    @Override
    public Film getFilmById(long filmId) {
        return films.stream()
                .filter(film -> film.getId() == filmId)
                .findFirst()
                .orElse(null);
    }
}
