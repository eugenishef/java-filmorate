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
            Film film = data.get(filmId);
            if (film == null) {
                film = Film.builder()
                        .id(filmId)
                        .name(rs.getString("name"))
                        .duration(rs.getInt("duration"))
                        .mpa(new Rating(rs.getInt("rating_id"), rs.getString("rating_name")))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .build();
                data.put(filmId, film);
            }

            int genreId = rs.getInt("genre_id");
            if (!rs.wasNull()) {
                Genre genre = Genre.builder()
                        .id(genreId)
                        .name(rs.getString("genre_name"))
                        .build();
                film.getGenres().add(genre);
            }

            int directorId = rs.getInt("director_id");
            if (!rs.wasNull()) {
                Director director = Director.builder()
                        .id(directorId)
                        .name(rs.getString("director_name"))
                        .build();
                film.getDirectors().add(director);
            }

            long userId = rs.getLong("user_id");
            if (!rs.wasNull()) {
                film.getUserLikes().add(userId);
            }
        }
        return data.values();
    }
}