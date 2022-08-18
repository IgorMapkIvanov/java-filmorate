package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewsRepository {
    Review addReview(Review review);
    void removeReviewById(int id);
    Review getReviewById(int id);
    void addLike(int reviewId, int userId);
    void addDislike(int reviewId, int userId);
    void removeLike(int reviewId, int userId);
    void removeDislike(int reviewId, int userId);
}
