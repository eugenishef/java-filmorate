package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;


@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString()
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    int id;
    long userId;
    String eventType;
    String operation;
    long entityId;
    Instant timestamp;
}