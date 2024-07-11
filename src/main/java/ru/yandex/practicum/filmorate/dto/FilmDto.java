package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    Set<Long> likes = new HashSet<>();
    Set<String> genres = new HashSet<>();
    String mpaRating;
}
