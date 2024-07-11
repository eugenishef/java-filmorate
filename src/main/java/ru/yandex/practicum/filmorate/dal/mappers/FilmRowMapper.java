package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Film film = new Film();

        film.setId(resultSet.getLong("id"));
        film.setName(resultSet.getString("name"));
        film.setDescription(resultSet.getString("description"));
        film.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        film.setDuration(resultSet.getInt("duration"));

        Set<Long> likes = new HashSet<>();
        film.setLikes(likes);

        Set<Genre> genres = new HashSet<>();
        String genreIds = resultSet.getString("genres");
        if (genreIds != null) {
            for (String genreId : genreIds.split(",")) {
                genres.add(new Genre(Long.parseLong(genreId), null));
            }
        }
        film.setGenres(genres);

        MPA mpa = new MPA(resultSet.getLong("mpa_rating_id"), resultSet.getString("mpa_rating_name"));
        film.setMpaRating(String.valueOf(mpa));

        return film;
    }
}
