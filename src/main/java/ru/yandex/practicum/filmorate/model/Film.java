package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    long id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    Set<Long> likes = new HashSet<>();
    Set<Genre> genres = new HashSet<>();
    String mpaRating;
}