package ru.yandex.practicum.filmorate.service.dao;

import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorService {
    DirectorDto create(Director newDirector);

    Collection<DirectorDto> findAll();

    DirectorDto findDirectorById(Integer id);

    DirectorDto update(Director updDirector);

    void delete(Integer id);
}
