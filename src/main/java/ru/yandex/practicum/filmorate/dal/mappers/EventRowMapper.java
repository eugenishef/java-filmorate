package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventRowMapper implements RowMapper<Event> {
    @Override
    public Event mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Event.builder()
                .id(resultSet.getInt("id"))
                .userId(resultSet.getInt("user_id"))
                .eventType(resultSet.getString("eventType_name"))
                .operation(resultSet.getString("operation_name"))
                .entityId(resultSet.getInt("entity_id"))
                .timestamp(resultSet.getTimestamp("timestamp").toInstant())
                .build();
    }
}
