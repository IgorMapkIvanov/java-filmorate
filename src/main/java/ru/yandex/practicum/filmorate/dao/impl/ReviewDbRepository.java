package ru.yandex.practicum.filmorate.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
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
public class ReviewDbRepository implements ReviewsRepository {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbRepository userDbRepository;

    @Autowired
    ReviewDbRepository(JdbcTemplate jdbcTemplate, UserDbRepository userDbRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.userDbRepository = userDbRepository;
    }

    @Override
    public Review addReview(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        int filmId = review.getFilmId();
        int userId = review.getUserId();

        idValidation(filmId);
        userValidation(userId);

        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO reviews(FILM_ID, USER_ID, IS_POSITIVE, CONTENT) VALUES ( ?, ?, ?, ? )"
            , new String[]{"ID"});

            statement.setInt(1, filmId);
            statement.setInt(2, userId);
            statement.setBoolean(3, review.getIsPositive());
            statement.setString(4, review.getContent());

            return statement;
        }, keyHolder);

        return getReviewById(keyHolder.getKey().intValue());
    }

    @Override
    public Collection<Review> getAllReviews(int filmId, int count) {
        if(filmId == 0) {
            return jdbcTemplate.query(
                    "SELECT r.* FROM REVIEWS r " +
                    "left join " +
                            "(select REVIEW_ID, sum(RATE) RATE from REVIEW_USEFUL group by REVIEW_ID) r_sum " +
                    "on r_sum.REVIEW_ID = r.ID " +
                    "ORDER BY r_sum.rate DESC " +
                    "LIMIT ?"
                    , ((rs, rowNum) -> makeReview(rs)), count);
        } else {
            return jdbcTemplate.query("SELECT * FROM REVIEWS r " +
                            "left join " +
                            "(select REVIEW_ID, sum(RATE) RATE from REVIEW_USEFUL group by REVIEW_ID) r_sum " +
                            "on r_sum.REVIEW_ID = r.ID " +
                            "WHERE r.FILM_ID = ?" +
                            "ORDER BY r_sum.rate DESC " +
                            "LIMIT ? "
                    , ((rs, rowNum) -> makeReview(rs)), filmId, count);
        }
    }

    @Override
    public Review updateReview(Review review) {
        reviewValidation(review.getId());

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
    public void removeReviewById(int id) {
        reviewValidation(id);
        jdbcTemplate.update("DELETE FROM REVIEWS WHERE ID = ?", id);
    }

    @Override
    public Review getReviewById(int id) throws NotFoundException {
        reviewValidation(id);

        return jdbcTemplate.queryForObject("SELECT * FROM REVIEWS WHERE ID = ?",
                (rs, rowNum) -> makeReview(rs), id);
    }

    @Override
    public void addLike(int reviewId, long userId) {
        reviewValidation(reviewId);
        userValidation(userId);

        jdbcTemplate.update("MERGE INTO REVIEW_USEFUL(REVIEW_ID, USER_ID, RATE) VALUES ( ?, ?, ? )",
                reviewId,
                userId,
                1);
    }

    @Override
    public void addDislike(int reviewId, long userId) {
        reviewValidation(reviewId);
        userValidation(userId);

        jdbcTemplate.update("MERGE INTO REVIEW_USEFUL(REVIEW_ID, USER_ID, RATE) VALUES ( ?, ?, ? )",
                reviewId,
                userId,
                -1);
    }

    @Override
    public void removeLike(int reviewId, long userId) {
        reviewValidation(reviewId);
        userValidation(userId);

        jdbcTemplate.update("DELETE FROM REVIEW_USEFUL WHERE REVIEW_ID = ? AND USER_ID = ? AND RATE = 1",
                reviewId,
                userId);
    }

    @Override
    public void removeDislike(int reviewId, long userId) {
        reviewValidation(reviewId);
        userValidation(userId);

        jdbcTemplate.update("DELETE FROM REVIEW_USEFUL WHERE REVIEW_ID = ? AND USER_ID = ? AND RATE = -1",
                reviewId,
                userId);
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        int rating;
        int reviewId = rs.getInt("id");
        int filmId = rs.getInt("film_Id");
        int userId = rs.getInt("user_Id");

        idValidation(filmId);
        userValidation(userId);

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
                .filmId(filmId)
                .userId(userId)
                .isPositive(rs.getBoolean("IS_POSITIVE"))
                .content(rs.getString("content"))
                .useful(rating)
                .build();
    }

    private void idValidation(long id) {
        if (id < 0) {
            throw new NotFoundException("Incorrect id:" + id);
        }
    }

    private void reviewValidation(int id) {
        idValidation(id);

        try {
            jdbcTemplate.queryForObject("SELECT * FROM REVIEWS WHERE ID = ?",
                    (rs, rowNum) -> makeReview(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Not found review with id:" + id);
        }
    }

    private void userValidation(long id) {
        idValidation(id);

        try {
            userDbRepository.getById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Not found user with id:" + id);
        }
    }
}