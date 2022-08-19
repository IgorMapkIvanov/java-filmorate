package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.ReviewDbRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReviewsService {
    private final ReviewDbRepository reviewDbRepository;

    public Review addReview(Review review) {
        return reviewDbRepository.addReview(review);
    }

    public Review getReviewById(int id) throws NotFoundException {
        return reviewDbRepository.getReviewById(id);
    }

    public Review updateReview(Review review) {
        return reviewDbRepository.updateReview(review);
    }

    public void removeReviewById(int id) {
        reviewDbRepository.removeReviewById(id);
    }

    public Collection<Review> getAllReviews(int filmId, int numOfReviews) {
        return (reviewDbRepository.getAllReviews(filmId, numOfReviews)).stream()
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    public void setLikeToReview(int filmId, long userId) {
        reviewDbRepository.addLike(filmId, userId);
    }

    public void setDislikeToReview(int filmId, long userId) {
        reviewDbRepository.addDislike(filmId, userId);
    }

    public void removeReviewLike(int filmId, long userId) {
        reviewDbRepository.removeLike(filmId, userId);
    }

    public void removeReviewDislike(int filmId, long userId) {
        reviewDbRepository.removeDislike(filmId, userId);
    }
}
