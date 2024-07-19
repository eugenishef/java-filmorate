package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"email"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    long id;
    @NotNull
    String email;
    @NotNull
    String login;
    String name;
    @Past
    LocalDate birthday;
    @Builder.Default
    Set<Long> friends = new HashSet<>();
}
