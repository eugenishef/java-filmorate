package ru.yandex.practicum.filmorate.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.RatingDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.service.dao.RatingService;
import ru.yandex.practicum.filmorate.dal.dao.RatingStorage;

import java.util.Collection;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {
    final RatingStorage ratingStorage;

    static final String RATING_NOT_FOUND_MSG = "Рейтинг фильма с id = %d не найден";

    @Override
    public Collection<RatingDto> findAll() {
        return ratingStorage.findAll().stream().map(RatingMapper::modelToDto).toList();
    }

    @Override
    public RatingDto findRatingById(Integer id) {
        return RatingMapper.modelToDto(ratingStorage.findRatingById(id)
                .orElseThrow(() -> new NotFoundException(String.format(RATING_NOT_FOUND_MSG, id))));
    }
}
