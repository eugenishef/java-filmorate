package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ToString()
@Data
@Builder
public class DirectorDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    int id;
    String name;
}
