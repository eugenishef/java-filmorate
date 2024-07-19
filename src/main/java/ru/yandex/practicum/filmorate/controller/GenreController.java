package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.dao.GenreService;

import javax.validation.constraints.Min;
import java.util.Collection;

@RequestMapping(GenreController.GENRES_BASE_PATH)
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequiredArgsConstructor
@Validated
public class GenreController {
    public static final String GENRES_BASE_PATH = "/genres";
    final GenreService genreService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<GenreDto> findAll() {
        return genreService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GenreDto findGenreById(@PathVariable @Min(1) Integer id) {
        return genreService.findGenreById(id);
    }
}
