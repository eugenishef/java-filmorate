package ru.yandex.practicum.filmorate.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class LikeService {
    private final Map<Long, Set<Long>> filmLikes = new HashMap<>();

    public void addLike(long userId, long filmId) {
        filmLikes.putIfAbsent(filmId, new HashSet<>());
        filmLikes.get(filmId).add(userId);
    }

    public void removeLike(long userId, long filmId) {
        if (filmLikes.containsKey(filmId)) {
            filmLikes.get(filmId).remove(userId);
            if (filmLikes.get(filmId).isEmpty()) {
                filmLikes.remove(filmId);
            }
        }
    }

    public int getLikesCount(long filmId) {
        return filmLikes.getOrDefault(filmId, new HashSet<>()).size();
    }

    public Set<Long> getUsersWhoLikedFilm(long filmId) {
        return filmLikes.getOrDefault(filmId, new HashSet<>());
    }
}
