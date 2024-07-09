package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.service.LikeService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
public class Film {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Long> likes = new HashSet<>();
    private Set<String> genres = new HashSet<>();
    private String mpaRating;

    private final LikeService likeService = new LikeService();

    public Film(int id, String name, String description, LocalDate releaseDate, int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

    public void addLike(long userId) {
        likes.add(userId);
    }

    public void removeLike(long userId) {
        likes.remove(userId);
    }

    public void likeFilm(long userId, long filmId) {
        likeService.addLike(userId, filmId);

        Film film = findFilmById(filmId);
        film.addLike(userId);
    }

    public void unlikeFilm(long userId, long filmId) {
        likeService.removeLike(userId, filmId);

        Film film = findFilmById(filmId);
        film.removeLike(userId);
    }

    public int getFilmLikesCount(long filmId) {
        return likeService.getLikesCount(filmId);
    }

    private Film findFilmById(long filmId) {
        return new Film();
    }

    public void addGenre(long genreId) {
        genres.add(String.valueOf(genreId));
    }

    public void removeGenre(long genreId) {
        genres.remove(genreId);
    }

    @Override
    public String toString() {
        return "Film{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", releaseDate=" + releaseDate +
                ", duration=" + duration +
                ", genres=" + genres +
                ", mpaRating='" + mpaRating + '\'' +
                '}';
    }
}
