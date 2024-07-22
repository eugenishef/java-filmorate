package ru.yandex.practicum.filmorate.dal.dao;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.Optional;

public interface DirectorStorage {
    Director create(Director newDirector);

    Collection<Director> findAll();

    Optional<Director> findDirectorById(Integer id);

    Director update(Director updDirector);

    void delete(Integer id);
}
