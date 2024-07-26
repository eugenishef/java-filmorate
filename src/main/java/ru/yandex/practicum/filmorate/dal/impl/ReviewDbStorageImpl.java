package ru.yandex.practicum.filmorate.dal.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.dao.ReviewStorage;
import ru.yandex.practicum.filmorate.dal.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Repository
@Primary
@RequiredArgsConstructor
public class ReviewDbStorageImpl implements ReviewStorage {
    final ReviewRowMapper reviewRowMapper;
    final JdbcTemplate jdbc;

    static final String REVIEW_DELETED_INFO_MSG = "Отзыв с id = {} удален";
    static final String REVIEW_USERLIKES_DELETED_INFO_MSG = "Оценки отзыва с id = {} удалены";
    static final String UPDATE_ERROR_MSG = "Не удалось обновить данные";
    static final String UPDATE_REVIEW_INFO_MSG = "Отзыв с id = {} обновлен";
    static final String REVIEW_LIKE_ADDED_INFO_MSG = "Лайк к отзыву с id = {} от пользователя с id = {} добавлен";
    static final String REVIEW_DISLIKE_ADDED_INFO_MSG = "Дизлайк к отзыву с id = {} от пользователя с id = {} добавлен";
    static final String REVIEW_LIKE_DELETED_INFO_MSG = "Лайк к отзыву с id = {} от пользователя с id = {} удален";
    static final String BASE_REVIEW_SELECT_SQL_TEMPLATE =
            "SELECT r.id, r.content, r.is_positive, r.user_id, r.film_id, " +
                    "SUM(CASE WHEN ru.is_useful = TRUE THEN 1 WHEN ru.is_useful = FALSE THEN -1 ELSE 0 END) as useful " +
                    "FROM review r " +
                    "LEFT JOIN review_userlikes ru ON r.id = ru.review_id " +
                    "WHERE %s " +
                    "GROUP BY r.id, r.content, r.is_positive, r.user_id, r.film_id ";

    @Override
    public Collection<Review> find(Integer filmId, Integer count) {
        String whereClause = "TRUE ";
        Object[] args;
        if (!Objects.isNull(filmId)) {
            whereClause += "AND film_id = ? ";
            args = new Object[]{filmId, count};
        } else {
            args = new Object[]{count};
        }
        String query = String.format(BASE_REVIEW_SELECT_SQL_TEMPLATE, whereClause) + " LIMIT ?";
        return jdbc.query(query, reviewRowMapper, args);
    }

    @Override
    public Review create(Review newReview) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String query = "INSERT INTO review(content, is_positive, user_id, film_id) " +
                "VALUES (?, ?, ?, ?)";
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setObject(1, newReview.getContent());
            ps.setObject(2, newReview.getIsPositive());
            ps.setObject(3, newReview.getUserId());
            ps.setObject(4, newReview.getFilmId());
            return ps;
        }, keyHolder);
        Long reviewId = keyHolder.getKeyAs(Long.class);
        Review review = findReviewById(reviewId).get();
        log.info("Отзыв {} добавлен", review);
        return review;
    }

    @Override
    public Review update(Review updReview) {
        String query = "UPDATE review " +
                "SET content = ?, is_positive = ?, user_id = ?, film_id = ? " +
                "WHERE id = ?";
        int rowsUpdated = jdbc.update(query,
                updReview.getContent(),
                updReview.getIsPositive(),
                updReview.getUserId(),
                updReview.getFilmId(),
                updReview.getReviewId());
        if (rowsUpdated == 0) {
            throw new RuntimeException(UPDATE_ERROR_MSG);
        } else {
            log.info(UPDATE_REVIEW_INFO_MSG, updReview.getReviewId());
            return findReviewById(updReview.getReviewId()).get();
        }
    }

    @Override
    public void delete(Long reviewId) {
        String query = "DELETE FROM review_userlikes WHERE review_id = ? ";
        int rowsDeleted = jdbc.update(query, reviewId);
        if (rowsDeleted > 0) {
            log.info(REVIEW_USERLIKES_DELETED_INFO_MSG, reviewId);
        }
        query = "DELETE FROM review WHERE id = ? ";
        rowsDeleted = jdbc.update(query, reviewId);
        if (rowsDeleted > 0) {
            log.info(REVIEW_DELETED_INFO_MSG, reviewId);
        }
    }

    @Override
    public Optional<Review> findReviewById(Long id) {
        String query = String.format(BASE_REVIEW_SELECT_SQL_TEMPLATE, " r.id = ? ");
        List<Review> result = jdbc.query(query, reviewRowMapper, id);
        if (result.size() == 0) {
            return Optional.empty();
        }
        return Optional.of(result.getFirst());
    }

    @Override
    public void addLike(Long reviewId, Long userId, Boolean isUseful) {
        String query =
                "MERGE INTO review_userlikes (review_id, user_id, is_useful) KEY (review_id, user_id) VALUES (?, ?, ?)";
        int rowsMerged = jdbc.update(query, reviewId, userId, isUseful);
        if (rowsMerged > 0) {
            if (isUseful) {
                log.info(REVIEW_LIKE_ADDED_INFO_MSG, reviewId, userId);
            } else {
                log.info(REVIEW_DISLIKE_ADDED_INFO_MSG, reviewId, userId);
            }
        }
    }

    @Override
    public void deleteLike(Long reviewId, Long userId) {
        String query = "DELETE FROM review_userlikes WHERE review_id = ? AND user_id = ? ";
        int rowsDeleted = jdbc.update(query, reviewId, userId);
        if (rowsDeleted > 0) {
            log.info(REVIEW_LIKE_DELETED_INFO_MSG, reviewId, userId);
        }
    }

    @Override
    public Optional<Boolean> getUserLikeOrDislike(Long reviewId, Long userId) {
        String query = "SELECT is_useful FROM review_userlikes WHERE review_id = ? AND user_id = ? ";
        try {
            return Optional.ofNullable(jdbc.queryForObject(query, Boolean.class, reviewId, userId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
