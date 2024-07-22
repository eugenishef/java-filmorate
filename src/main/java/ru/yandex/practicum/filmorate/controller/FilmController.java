package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.dao.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;

@RequestMapping(FilmController.FILMS_BASE_PATH)
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequiredArgsConstructor
@Validated
public class FilmController {
    static final String FILMS_BASE_PATH = "/films";
    static final String FILM_LIKE_PATH = "/{id}/like/{userId}";

    final FilmService filmService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public FilmDto findFilmById(@PathVariable @Min(1) Integer id) {
        return filmService.findFilmById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Valid @RequestBody Film newFilm) {
        return filmService.create(newFilm);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public FilmDto update(@Valid @RequestBody Film updFilm) {
        return filmService.update(updFilm);
    }

    @PutMapping(FILM_LIKE_PATH)
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable @Min(1) Integer id, @PathVariable @Min(1) Long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping(FILM_LIKE_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLike(@PathVariable @Min(1) Integer id, @PathVariable @Min(1) Long userId) {
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public Collection<FilmDto> topPopularFilms(@RequestParam(defaultValue = "10") @Min(1) int count) {
        return filmService.getTopPopularFilms(count);
    }

    @GetMapping("/director/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<FilmDto> listFilmsDirector(@PathVariable @Min(1) Integer directorId,
                                               @RequestParam String sortBy) {
        return filmService.listFilmsDirector(directorId, sortBy);
    }

    @GetMapping("/common")
    @ResponseStatus(HttpStatus.OK)
    public Collection<FilmDto> commonFilms(
            @RequestParam() long userId, @RequestParam() long friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
}
