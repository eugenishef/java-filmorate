package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.dal.dao.RatingStorage;
import ru.yandex.practicum.filmorate.dal.mappers.RatingRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class RatingDbStorageImpl implements RatingStorage {
    final JdbcTemplate jdbc;
    final RatingRowMapper ratingMapper;

    @Override
    public Collection<Rating> findAll() {
        String query = "SELECT * FROM rating";
        return jdbc.query(query, ratingMapper);
    }

    @Override
    public Optional<Rating> findRatingById(Integer id) {
        String query = "SELECT * FROM rating WHERE id = ?";
        List<Rating> result = jdbc.query(query, ratingMapper, id);
        if (result.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(result.getFirst());
    }

}
