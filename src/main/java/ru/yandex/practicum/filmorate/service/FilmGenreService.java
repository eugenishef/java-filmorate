package ru.yandex.practicum.filmorate.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FilmGenreService {
    private final Map<Long, Set<Long>> filmGenres = new HashMap<>();

    public void addGenre(long filmId, long genreId) {
        filmGenres.putIfAbsent(filmId, new HashSet<>());
        filmGenres.get(filmId).add(genreId);
    }

    public void removeGenre(long filmId, long genreId) {
        if (filmGenres.containsKey(filmId)) {
            filmGenres.get(filmId).remove(genreId);
            if (filmGenres.get(filmId).isEmpty()) {
                filmGenres.remove(filmId);
            }
        }
    }

    public Set<Long> getGenres(long filmId) {
        return filmGenres.getOrDefault(filmId, new HashSet<>());
    }

    public Set<Long> getFilmsByGenre(long genreId) {
        Set<Long> films = new HashSet<>();
        for (Map.Entry<Long, Set<Long>> entry : filmGenres.entrySet()) {
            if (entry.getValue().contains(genreId)) {
                films.add(entry.getKey());
            }
        }
        return films;
    }
}
