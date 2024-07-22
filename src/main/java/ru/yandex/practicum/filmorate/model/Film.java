package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@EqualsAndHashCode(of = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
    int id;
    @NotNull
    String name;
    String description;
    @Past
    LocalDate releaseDate;
    @Min(0)
    Integer duration;
    @Builder.Default
    Set<Long> userLikes = new HashSet<>();
    Rating mpa;
    @Builder.Default
    Set<Genre> genres = new HashSet<>();
    @Builder.Default
    Set<Director> directors = new HashSet<>();
}