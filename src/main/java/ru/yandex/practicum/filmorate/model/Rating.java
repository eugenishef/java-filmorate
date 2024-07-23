package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString()
@Data
@Builder
@RequiredArgsConstructor
public class Rating {
    final int id;
    final String name;
}
