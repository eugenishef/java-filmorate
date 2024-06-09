package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.util.IdGenerator;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private List<Film> films = new ArrayList<>();
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private String validationBaseMessage = "Валидация при добавлении фильма не пройдена: ";

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        String validationErrorMessage = validationBaseMessage;
        if (film.getName() == null || film.getName().length() == 0) {
            validationErrorMessage += "Название фильма не может быть пустым";

            log.error(validationErrorMessage);
            throw new ValidationException(validationErrorMessage);

        }
        if (film.getDescription().length() > 200) {
            validationErrorMessage += "Максимальная длина описания — 200 символов";

            log.error(validationErrorMessage);
            throw new ValidationException(validationErrorMessage);
        }
        if (film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
            validationErrorMessage += "Дата релиза — не раньше 28 декабря 1895 года";

            log.error(validationErrorMessage);
            throw new ValidationException(validationErrorMessage);
        }
        if (film.getDuration() <= 0) {
            validationErrorMessage += "Продолжительность фильма должна быть положительным числом";

            log.error(validationErrorMessage);
            throw new ValidationException(validationErrorMessage);
        }

        film.setId(IdGenerator.getNextId(films, Film::getId));
        films.add(film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == film.getId()) {
                films.set(i, film);
                log.info("Фильм c id {} успешно обновлен", film.getId());
                return film;
            }
        }
        throw new IllegalArgumentException("Фильм с id: " + film.getId() + " не найден");
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return films;
    }
}
