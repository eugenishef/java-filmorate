package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString()
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Genre {
    int id;
    String name;
}
