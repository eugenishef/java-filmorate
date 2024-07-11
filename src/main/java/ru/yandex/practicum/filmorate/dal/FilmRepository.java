package ru.yandex.practicum.filmorate.dal;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Repository
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmRepository {

    final JdbcTemplate jdbc;
    final FilmRowMapper filmRowMapper;

    @Autowired
    public FilmRepository(JdbcTemplate jdbcTemplate, FilmRowMapper filmRowMapper) {
        this.jdbc = jdbcTemplate;
        this.filmRowMapper = filmRowMapper;
    }

    public Film createFilm(Film film) {
        String sql = "INSERT INTO Films (name, description, release_date, duration, mpa_rating) VALUES (?, ?, ?, ?, ?) RETURNING id";
        Long filmId = jdbc.queryForObject(sql, new Object[]{film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpaRating()}, Long.class);
        film.setId(filmId);
        return film;
    }

    public Film updateFilm(Film film) {
        String sql = "UPDATE Films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating = ? WHERE id = ?";
        jdbc.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpaRating(), film.getId());
        return film;
    }

    public List<Film> getAllFilms() {
        String sql = "SELECT * FROM Films";
        return jdbc.query(sql, filmRowMapper);
    }

    public Film getFilmById(long id) {
        String sql = "SELECT * FROM Films WHERE id = ?";
        return jdbc.queryForObject(sql, filmRowMapper, id);
    }
}
