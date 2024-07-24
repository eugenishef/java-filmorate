package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
public class ReviewDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    long reviewId;
    String content;
    Boolean isPositive;
    long userId;
    int filmId;
    int useful;
}
