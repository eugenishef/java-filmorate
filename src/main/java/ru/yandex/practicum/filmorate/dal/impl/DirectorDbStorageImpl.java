package ru.yandex.practicum.filmorate.dal.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dal.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class DirectorDbStorageImpl implements DirectorStorage {

    private final JdbcTemplate jdbc;
    private final DirectorRowMapper directorMapper;

    @Override
    public Director create(Director newDirector) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO director(name) VALUES (?)";
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, newDirector.getName());
            return ps;
        }, keyHolder);
        Integer id = keyHolder.getKeyAs(Integer.class);
        if (id != null) {
            log.info("Режиссер {} добавлен", newDirector);
            return Director.builder()
                    .id(id)
                    .name(newDirector.getName())
                    .build();
        } else {
            throw new RuntimeException("Не удалось сохранить данные");
        }
    }

    @Override
    public Collection<Director> findAll() {
        String query = "SELECT * FROM director";
        return jdbc.query(query, directorMapper);
    }

    @Override
    public Optional<Director> findDirectorById(Integer id) {
        String query = "SELECT * FROM director WHERE id = ?";
        List<Director> result = jdbc.query(query, directorMapper, id);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(result.getFirst());
    }

    @Override
    public Director update(Director updDirector) {
        String query = "UPDATE director SET name = ? WHERE id = ?";
        int rowsUpdated = jdbc.update(query,
                updDirector.getName(),
                updDirector.getId());
        if (rowsUpdated == 0) {
            throw new RuntimeException("Не удалось обновить данные");
        } else {
            log.info("Режиссер с id = {} обновлен", updDirector.getId());
            return updDirector;
        }
    }

    @Override
    public void delete(Integer id) {
        String query = "DELETE FROM director WHERE id = ?; " +
                "DELETE FROM film_director WHERE director_id = ?";
        jdbc.update(query, id, id);
    }
}
