package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.dal.dao.GenreStorage;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class GenreDbStorageImpl implements GenreStorage {
    final JdbcTemplate jdbc;
    final GenreRowMapper genreMapper;

    @Override
    public Collection<Genre> findAll() {
        String query = "SELECT * FROM genre";
        return jdbc.query(query, genreMapper);
    }

    @Override
    public Optional<Genre> findGenreById(Integer id) {
        String query = "SELECT * FROM genre WHERE id = ?";
        List<Genre> result = jdbc.query(query, genreMapper, id);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.getFirst());
    }

}
