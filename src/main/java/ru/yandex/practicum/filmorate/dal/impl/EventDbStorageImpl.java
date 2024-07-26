package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.dao.EventStorage;
import ru.yandex.practicum.filmorate.dal.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.model.EntityType;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Operation;

import java.time.Instant;
import java.util.Collection;
import java.util.LinkedHashSet;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class EventDbStorageImpl implements EventStorage {
    final JdbcTemplate jdbc;
    final EventRowMapper eventRowMapper;

    @Override
    public void add(Long userId, Long entityId, EntityType entityType, Operation operation) {
        String query =
                "INSERT INTO events(user_id, event_type_id, operation_id, entity_id, timestamp)" +
                        " VALUES (?, ?, ?, ?, ?)";
        jdbc.update(query, userId, entityType.ordinal(), operation.ordinal(), entityId, Instant.now());
    }

    @Override
    public Collection<Event> findById(Long id) {
        String query = "SELECT ev.*, et.name as eventType_name, o.name as operation_name from" +
                " (SELECT * from EVENTS WHERE user_id = ? GROUP BY timestamp ORDER BY timestamp) as ev" +
                " LEFT JOIN event_type et on et.id = ev.event_type_id" +
                " LEFT JOIN operation o on o.id = ev.operation_id";
        return new LinkedHashSet<>(jdbc.query(query, eventRowMapper, id));
    }
}