package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private List<Film> films = new ArrayList<>();
    private int currentId = 1;

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        film.setId(currentId++);
        films.add(film);
        log.info("Добавлен новый фильм: {}", film.getName());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        for (int i = 0; i < films.size(); i++) {
            if (films.get(i).getId() == film.getId()) {
                films.set(i, film);
                log.info("Фильм c id {} успешно обновлен", film.getId(), film.getName());
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
