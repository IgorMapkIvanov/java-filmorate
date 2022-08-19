package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewsRepository {
    Review addReview(Review review);
    Collection<Review> getAllReviews(long filmId, int count);
    Review updateReview(Review review);
    void removeReviewById(long id);
    Review getReviewById(long id);
    void addLike(long reviewId, long userId);
    void addDislike(long reviewId, long userId);
    void removeLike(long reviewId, long userId);
    void removeDislike(long reviewId, long userId);
}
