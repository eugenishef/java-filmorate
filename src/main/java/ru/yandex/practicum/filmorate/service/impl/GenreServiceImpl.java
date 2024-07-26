package ru.yandex.practicum.filmorate.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.service.dao.GenreService;
import ru.yandex.practicum.filmorate.dal.dao.GenreStorage;

import java.util.Collection;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
    final GenreStorage genreStorage;

    static final String GENRE_NOT_FOUND_MSG = "Жанр фильма с id = %d не найден";

    @Override
    public Collection<GenreDto> findAll() {
        return genreStorage.findAll().stream().map(GenreMapper::modelToDto).toList();
    }

    @Override
    public GenreDto findGenreById(Integer id) {
        return GenreMapper.modelToDto(genreStorage.findGenreById(id)
                .orElseThrow(() -> new NotFoundException(String.format(GENRE_NOT_FOUND_MSG, id))));
    }
}
