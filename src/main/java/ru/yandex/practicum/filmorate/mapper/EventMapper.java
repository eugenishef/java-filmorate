package ru.yandex.practicum.filmorate.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.dto.EventDto;
import ru.yandex.practicum.filmorate.model.Event;

@UtilityClass
public final class EventMapper {
    public static EventDto modelToDto(Event event) {
        return EventDto.builder()
                .id(event.getId())
                .userId(event.getUserId())
                .eventType(event.getEventType())
                .operation(event.getOperation())
                .entityId(event.getEntityId())
                .timestamp(event.getTimestamp().toEpochMilli())
                .build();
    }
}