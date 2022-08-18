package ru.yandex.practicum.filmorate.dao.impl;

import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.ReviewsRepository;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;

@Repository
@AllArgsConstructor
public class ReviewDbRepository implements ReviewsRepository {

    private static JdbcTemplate jdbcTemplate;

    @Override
    public Review addReview(Review review) {

        return jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO reviews(FILM_ID, USER_ID, isPositive, CONTENT) VALUES ( ?, ?, ?, ? )"
            );

            statement.setInt(1, review.getFilmId());
            statement.setInt(2, review.getUserId());
            statement.setBoolean(3, review.getIsPositive());
            statement.set



        });
    }

    @Override
    public void removeReviewById(int id) {

    }

    @Override
    public Review getReviewById(int id) {
        return null;
    }

    @Override
    public void addLike(int reviewId, int userId) {

    }

    @Override
    public void addDislike(int reviewId, int userId) {

    }

    @Override
    public void removeLike(int reviewId, int userId) {

    }

    @Override
    public void removeDislike(int reviewId, int userId) {

    }

    private Review makeReview(Review review) {
        return Review.builder().build()
                .set


    }

}
