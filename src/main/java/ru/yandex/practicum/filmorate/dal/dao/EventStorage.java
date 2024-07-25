package ru.yandex.practicum.filmorate.dal.dao;

import ru.yandex.practicum.filmorate.model.EntityType;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.Collection;

public interface EventStorage {
    void add(Long userId, Long entityId, EntityType entityType, Operation operation);

    Collection<Event> findById(Long id);
}