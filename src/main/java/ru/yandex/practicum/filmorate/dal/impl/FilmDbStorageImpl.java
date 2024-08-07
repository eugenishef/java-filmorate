package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.DirectorRowMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.dal.dao.FilmStorage;
import ru.yandex.practicum.filmorate.dal.extractors.FilmExtractor;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class FilmDbStorageImpl implements FilmStorage {
    final JdbcTemplate jdbc;
    final FilmRowMapper filmRowMapper;
    final GenreRowMapper genreRowMapper;
    final DirectorRowMapper directorRowMapper;
    final FilmExtractor filmExtractor;

    private Set<Genre> findFilmGenresIdsByFilmId(Integer filmId) {
        return new HashSet<>(jdbc.query("SELECT g.* FROM film_genre fg, genre g " +
                "WHERE fg.genre_id = g.id AND fg.film_id = ? ", genreRowMapper, filmId));
    }

    private Set<Director> findFilmDirectorsIdsByFilmId(Integer filmId) {
        return new HashSet<>(jdbc.query("SELECT d.* FROM film_director fd, director d " +
                "WHERE fd.director_id = d.id AND fd.film_id = ? ", directorRowMapper, filmId));
    }

    public Set<Long> findFilmLikesByFilmId(Integer filmId) {
        return new HashSet<>(jdbc.queryForList("SELECT fu.user_id FROM film_userlikes fu " +
                "WHERE fu.film_id = ? ", Long.class, filmId));
    }

    @Override
    public List<Film> searchFilms(String query, String by) {
        String baseSql = "SELECT f.*, r.name as rating_name, g.id as genre_id, g.name as genre_name, d.id as director_id, d.name as director_name " +
                "FROM film f " +
                "LEFT JOIN rating r ON f.rating_id = r.id " +
                "LEFT JOIN film_genre fg ON f.id = fg.film_id " +
                "LEFT JOIN genre g ON fg.genre_id = g.id " +
                "LEFT JOIN film_director fd ON f.id = fd.film_id " +
                "LEFT JOIN director d ON fd.director_id = d.id " +
                "WHERE (";

        String titleSql = "LOWER(f.name) LIKE LOWER(?) OR LOWER(f.description) LIKE LOWER(?)";
        String directorSql = "LOWER(d.name) LIKE LOWER(?)";

        List<String> conditions = new ArrayList<>();
        List<Object> params = new ArrayList<>();

        if (by.contains("title")) {
            conditions.add(titleSql);
            params.add("%" + query + "%");
            params.add("%" + query + "%");
        }
        if (by.contains("director")) {
            conditions.add(directorSql);
            params.add("%" + query + "%");
        }

        if (conditions.isEmpty()) {
            return List.of();
        }

        String finalSql = baseSql + String.join(" OR ", conditions) + ")";
        Collection<Film> films = jdbc.query(finalSql, filmExtractor, params.toArray());
        return new ArrayList<>(films);
    }

    @Override
    public Collection<Film> findAll() {
        String query = "SELECT f.*, r.name as rating_name, fg.genre_id, g.name as genre_name, " +
                "fd.director_id, d.name as director_name, fu.user_id FROM film f " +
                "LEFT JOIN rating r ON f.rating_id = r.id " +
                "LEFT JOIN film_genre fg ON fg.film_id = f.id " +
                "LEFT JOIN genre g ON g.id = fg.genre_id " +
                "LEFT JOIN film_director fd ON fd.film_id = f.id " +
                "LEFT JOIN director d ON d.id = fd.director_id " +
                "LEFT JOIN film_userlikes fu ON f.id = fu.film_id ";
        return jdbc.query(query, filmExtractor);
    }

    @Override
    public void addLike(Integer filmId, Long userId) {
        String query = "MERGE INTO film_userlikes (film_id, user_id) KEY (film_id, user_id) VALUES (?, ?)";
        int rowsMerged = jdbc.update(query, filmId, userId);
        if (rowsMerged > 0) {
            log.info("Лайк к фильму с id = {} от пользователя с id = {} добавлен", filmId, userId);
        }
    }

    @Override
    public void deleteLike(Integer filmId, Long userId) {
        String query = "DELETE FROM film_userlikes WHERE film_id = ? AND user_id = ? ";
        int rowsDeleted = jdbc.update(query, filmId, userId);
        if (rowsDeleted > 0) {
            log.info("Лайк к фильму с id = {} от пользователя с id = {} удален", filmId, userId);
        }
    }

    @Override
    public Film create(Film newFilm) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO film(name, description, release_date, duration, rating_id) " +
                "VALUES (?, ?, ?, ?, ? )";
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, newFilm.getName());
            ps.setObject(2, newFilm.getDescription());
            ps.setObject(3, newFilm.getReleaseDate());
            ps.setObject(4, newFilm.getDuration());
            ps.setObject(5, newFilm.getMpa().getId());
            return ps;
        }, keyHolder);
        Integer filmId = keyHolder.getKeyAs(Integer.class);
        updateFilmGenre(filmId, newFilm.getGenres());
        updateFilmDirector(filmId, newFilm.getDirectors());
        Film film = findFilmById(filmId).get();
        log.info("Фильм {} добавлен", film);
        return film;
    }

    private void updateFilmGenre(Integer filmId, Collection<Genre> genres) {
        if (!Objects.isNull(genres)) {
            List<Genre> genresList = genres.stream().toList();
            String query = "MERGE INTO film_genre(film_id, genre_id) KEY(film_id, genre_id) VALUES (?, ?)";
            jdbc.batchUpdate(query, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, filmId);
                    ps.setInt(2, genresList.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return genres.size();
                }
            });
        }
    }

    @Override
    public Film update(Film updFilm) {
        String query = "UPDATE film " +
                "SET name = ?, description = ?, release_date = ?, duration = ?, rating_id =? " +
                "WHERE id = ?";
        int rowsUpdated = jdbc.update(query,
                updFilm.getName(),
                updFilm.getDescription(),
                updFilm.getReleaseDate(),
                updFilm.getDuration(),
                updFilm.getMpa().getId(),
                updFilm.getId());
        if (!Objects.isNull(updFilm.getGenres())) {
            updateFilmGenre(updFilm.getId(), updFilm.getGenres());
        }
        if (!Objects.isNull(updFilm.getDirectors())) {
            updateFilmDirector(updFilm.getId(), updFilm.getDirectors());
        }
        if (rowsUpdated == 0) {
            throw new RuntimeException("Не удалось обновить данные");
        } else {
            log.info("Фильм с id = {} обновлен", updFilm.getId());
            return findFilmById(updFilm.getId()).get();
        }
    }

    @Override
    public Optional<Film> findFilmById(Integer id) {
        String query =
                "SELECT f.*, r.name as rating_name FROM film f, rating r WHERE f.rating_id = r.id AND f.id = ? ";
        List<Film> result = jdbc.query(query, filmRowMapper, id);
        if (result.isEmpty()) {
            return Optional.empty();
        }
        Film film = result.getFirst();
        Set<Genre> genres = findFilmGenresIdsByFilmId(id);
        film.setGenres(genres);
        Set<Long> userLikes = findFilmLikesByFilmId(id);
        film.setUserLikes(userLikes);
        Set<Director> directors = findFilmDirectorsIdsByFilmId(id);
        film.setDirectors(directors);
        return Optional.of(film);
    }

    @Override
    public Collection<Film> findCommonFilms(Long userId, Long friendId) {
        String query =
                "SELECT f.*, r.name as rating_name, fg.genre_id, g.name as genre_name, fu.user_id, " +
                        "fd.director_id, d.name as director_name " +
                        "FROM film_userlikes f_user " +
                        "JOIN film_userlikes f_friend ON f_user.film_id = f_friend.film_id " +
                        "JOIN film f ON f.id = f_user.film_id " +
                        "JOIN rating r ON f.rating_id = r.id " +
                        "LEFT JOIN film_genre fg ON fg.film_id = f.id " +
                        "LEFT JOIN genre g ON g.id = fg.genre_id " +
                        "LEFT JOIN film_director fd ON f.id = fd.film_id " +
                        "LEFT JOIN director d ON fd.director_id = d.id " +
                        "LEFT JOIN film_userlikes fu ON f.id = fu.film_id " +
                        "WHERE f_user.user_id = ? AND f_friend.user_id = ?";
        return jdbc.query(query, filmExtractor, userId, friendId);
    }

    private void updateFilmDirector(Integer filmId, Collection<Director> directors) {
        if (!Objects.isNull(directors)) {
            List<Director> directorsList = directors.stream().toList();
            String query = "MERGE INTO film_director(film_id, director_id) KEY(film_id, director_id) VALUES (?, ?)";
            jdbc.batchUpdate(query, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, filmId);
                    ps.setInt(2, directorsList.get(i).getId());
                }

                @Override
                public int getBatchSize() {
                    return directors.size();
                }
            });
        }
    }

    @Override
    public Collection<Film> listFilmsDirector(int directorId) {
        String query = "SELECT f.*, r.name as rating_name, fg.genre_id, g.name as genre_name, " +
                "fd.director_id, d.name as director_name, fu.user_id FROM film f " +
                            "LEFT JOIN rating r ON f.rating_id = r.id " +
                            "LEFT JOIN film_genre fg ON fg.film_id = f.id " +
                            "LEFT JOIN genre g ON g.id = fg.genre_id " +
                            "LEFT JOIN film_director fd ON fd.film_id = f.id " +
                            "LEFT JOIN director d ON d.id = fd.director_id " +
                            "LEFT JOIN film_userlikes fu ON f.id = fu.film_id " +
                            "WHERE fd.director_id = ? ";

        return jdbc.query(query, filmExtractor, directorId);
    }

    @Override
    public void deleteFilm(Integer filmId) {
        String query = "DELETE FROM film_director WHERE film_id = ?;" +
                "DELETE FROM film_genre  WHERE film_id = ?;" +
                "DELETE FROM film_userlikes  WHERE film_id = ?;" +
                "DELETE FROM review  WHERE film_id = ?;" +
                "DELETE FROM film WHERE id = ?";
        jdbc.update(query, filmId, filmId, filmId, filmId, filmId);
    }
}
