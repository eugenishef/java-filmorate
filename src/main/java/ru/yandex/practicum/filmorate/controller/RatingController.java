package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.service.dao.RatingService;

import javax.validation.constraints.Min;
import java.util.Collection;

@RequestMapping(RatingController.MPA_BASE_PATH)
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequiredArgsConstructor
@Validated
public class RatingController {
    public static final String MPA_BASE_PATH = "/mpa";
    final RatingService ratingService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<RatingDto> findAll() {
        return ratingService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RatingDto findRatingById(@PathVariable @Min(1) Integer id) {
        return ratingService.findRatingById(id);
    }
}
