package ru.yandex.practicum.filmorate.service.dao;

import ru.yandex.practicum.filmorate.dto.RatingDto;

import java.util.Collection;

public interface RatingService {
    Collection<RatingDto> findAll();

    RatingDto findRatingById(Integer id);
}
