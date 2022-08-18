package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewsRepository {
    Review addReview(Review review);

    Collection<Review> getAllReviews(int filmId, int count);

    Review updateReview(Review review);
    void removeReviewById(int id);
    Review getReviewById(int id);
    void addLike(int reviewId, long userId);
    void addDislike(int reviewId, long userId);
    void removeLike(int reviewId, long userId);
    void removeDislike(int reviewId, long userId);
}
