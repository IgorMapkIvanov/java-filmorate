package ru.yandex.practicum.filmorate.dao.impl;


import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.ReviewsRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Component
@AllArgsConstructor
public class ReviewDbRepository implements ReviewsRepository {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review addReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO reviews(FILM_ID, USER_ID, IS_POSITIVE, CONTENT) VALUES ( ?, ?, ?, ? )"
            , new String[]{"ID"});

            statement.setLong(1, review.getFilmId());
            statement.setLong(2, review.getUserId());
            statement.setBoolean(3, review.getIsPositive());
            statement.setString(4, review.getContent());

            return statement;
        }, keyHolder);

        return getReviewById(keyHolder.getKey().longValue());
    }

    @Override
    public Collection<Review> getAllReviews(long filmId, int count) {
        if(filmId == 0) {
            return jdbcTemplate.query(
                    "SELECT r.* FROM REVIEWS r " +
                    "left join " +
                            "(select REVIEW_ID, sum(RATE) RATE from REVIEW_USEFUL group by REVIEW_ID) r_sum " +
                    "on r_sum.REVIEW_ID = r.ID " +
                    "LIMIT ?"
                    , ((rs, rowNum) -> makeReview(rs)), count);
        } else {
            return jdbcTemplate.query("SELECT * FROM REVIEWS r " +
                            "left join " +
                            "(select REVIEW_ID, sum(RATE) RATE from REVIEW_USEFUL group by REVIEW_ID) r_sum " +
                            "on r_sum.REVIEW_ID = r.ID " +
                            "WHERE r.FILM_ID = ?" +
                            "LIMIT ? "
                    , ((rs, rowNum) -> makeReview(rs)), filmId, count);
        }
    }

    @Override
    public Review updateReview(Review review) {
        reviewDBValidation(review.getId());

        if (review.getContent() != null) {
            jdbcTemplate.update("UPDATE REVIEWS SET " +
                            "content = ? " +
                            "WHERE ID = ?",
                    review.getContent(),
                    review.getId());
        }

        if (review.getIsPositive() != null) {
            jdbcTemplate.update("UPDATE REVIEWS SET " +
                            "IS_POSITIVE = ? " +
                            "WHERE ID = ?",
                    review.getIsPositive(),
                    review.getId());
        }

        return getReviewById(review.getId());
    }

    @Override
    public void removeReviewById(long id) {
        reviewDBValidation(id);

        jdbcTemplate.update("DELETE FROM REVIEWS WHERE ID = ?", id);
    }

    @Override
    public Review getReviewById(long id) throws NotFoundException {
        reviewDBValidation(id);

        return jdbcTemplate.queryForObject("SELECT * FROM REVIEWS WHERE ID = ?",
                (rs, rowNum) -> makeReview(rs), id);
    }

    @Override
    public void addLike(long reviewId, long userId) {
        reviewDBValidation(reviewId);

        jdbcTemplate.update("MERGE INTO REVIEW_USEFUL(REVIEW_ID, USER_ID, RATE) VALUES ( ?, ?, ? )",
                reviewId,
                userId,
                1);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        reviewDBValidation(reviewId);

        jdbcTemplate.update("MERGE INTO REVIEW_USEFUL(REVIEW_ID, USER_ID, RATE) VALUES ( ?, ?, ? )",
                reviewId,
                userId,
                -1);
    }

    @Override
    public void removeLike(long reviewId, long userId) {
        reviewDBValidation(reviewId);

        jdbcTemplate.update("DELETE FROM REVIEW_USEFUL WHERE REVIEW_ID = ? AND USER_ID = ? AND RATE = 1",
                reviewId,
                userId);
    }

    @Override
    public void removeDislike(long reviewId, long userId) {
        reviewDBValidation(reviewId);

        jdbcTemplate.update("DELETE FROM REVIEW_USEFUL WHERE REVIEW_ID = ? AND USER_ID = ? AND RATE = -1",
                reviewId,
                userId);
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        int rating;
        Long reviewId = rs.getLong("id");

        int numOfRates = jdbcTemplate.queryForObject("SELECT COUNT(RATE) FROM REVIEW_USEFUL WHERE REVIEW_ID = ?"
                , Integer.class, reviewId);

        if (numOfRates == 0) {
            rating = 0;
        } else {
            rating = jdbcTemplate.queryForObject("SELECT SUM(RATE) FROM REVIEW_USEFUL WHERE REVIEW_ID = ?"
                    , Integer.class, reviewId);
        }

        return Review.builder()
                .id(reviewId)
                .filmId(rs.getLong("film_Id"))
                .userId(rs.getLong("user_Id"))
                .isPositive(rs.getBoolean("IS_POSITIVE"))
                .content(rs.getString("content"))
                .useful(rating)
                .build();
    }

    private void reviewDBValidation(long id) {
        try {
            jdbcTemplate.queryForObject("SELECT * FROM REVIEWS WHERE ID = ?",
                    (rs, rowNum) -> makeReview(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Not found review with id:" + id);
        }
    }

}