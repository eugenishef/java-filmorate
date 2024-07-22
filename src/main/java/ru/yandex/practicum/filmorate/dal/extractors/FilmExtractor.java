package ru.yandex.practicum.filmorate.dal.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class FilmExtractor implements ResultSetExtractor<Collection<Film>> {
    @Override
    public Collection<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Film> data = new LinkedHashMap<>();
        while (rs.next()) {
            int filmId = rs.getInt("id");
            if (data.containsKey(filmId)) {
                Film film = data.get(filmId);
                Set<Genre> genres = film.getGenres();
                genres.add(Genre.builder().id(rs.getInt("genre_id")).name(rs.getString("genre_name")).build());
                if (rs.getLong("user_id") != 0) {
                    Set<Long> userLikes = film.getUserLikes();
                    if (Objects.isNull(userLikes)) {
                        userLikes = new HashSet<>();
                    }
                    userLikes.add(rs.getLong("user_id"));
                }
                Set<Director> directors = film.getDirectors();
                setDirector(film, directors, rs);
            } else {
                Film film = Film.builder()
                        .id(filmId)
                        .name(rs.getString("name"))
                        .duration(rs.getInt("duration"))
                        .mpa(new Rating(rs.getInt("rating_id"), rs.getString("rating_name")))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .build();
                Set<Genre> genres = new HashSet<>();
                genres.add(Genre.builder().id(rs.getInt("genre_id")).name(rs.getString("genre_name")).build());
                film.setGenres(genres);
                if (rs.getLong("user_id") != 0) {
                    Set<Long> userLikes = new HashSet<>();
                    userLikes.add(rs.getLong("user_id"));
                    film.setUserLikes(userLikes);
                }
                Set<Director> directors = film.getDirectors();
                setDirector(film, directors, rs);
                data.put(filmId, film);
            }
        }
        return data.values();
    }

    private void setDirector(Film film, Set<Director> directors, ResultSet rs) throws SQLException {
        if (rs.getInt("director_id") != 0) {
            directors.add(Director.builder()
                    .id(rs.getInt("director_id"))
                    .name(rs.getString("director_name")).build());
            film.setDirectors(directors);
        }
    }
}