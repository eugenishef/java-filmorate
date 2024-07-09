package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.HashMap;
import java.util.Map;

public class GenreService {
    private final Map<Long, Genre> genres = new HashMap<>();

    public void addGenre(long id, String name) {
        genres.put(id, new Genre(id, name));
    }

    public void removeGenre(long id) {
        genres.remove(id);
    }

    public Genre getGenre(long id) {
        return genres.get(id);
    }

    public Map<Long, Genre> getAllGenres() {
        return new HashMap<>(genres);
    }
}
