package ru.yandex.practicum.filmorate.service.dao;

import ru.yandex.practicum.filmorate.dto.GenreDto;

import java.util.Collection;

public interface GenreService {
    Collection<GenreDto> findAll();

    GenreDto findGenreById(Integer id);
}
