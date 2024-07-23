package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString()
@Data
@Builder
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    Set<Long> friends;
}
