package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.EventDbRepository;
import ru.yandex.practicum.filmorate.dao.impl.FilmDbRepository;
import ru.yandex.practicum.filmorate.dao.impl.ReviewDbRepository;
import ru.yandex.practicum.filmorate.dao.impl.UserDbRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;
import java.util.Comparator;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ReviewsService {
    private final ReviewDbRepository reviewDbRepository;
    private final EventDbRepository eventDbRepository;
    private final UserDbRepository userDbRepository;
    private final FilmDbRepository filmDbRepository;

    public Review addReview(Review review) {
        newReviewBaseValidation(review);
        Review addedReview = reviewDbRepository.addReview(review);
        log.info("SERVICE: Add new review into storage: {}", addedReview);

        eventDbRepository.addEvent(addedReview.getUserId(),
                addedReview.getId(),
                EventType.REVIEW,
                EventOperation.ADD);
        return addedReview;
    }

    public Review getReviewById(long id) throws NotFoundException {
        Review review = reviewDbRepository.getReviewById(id);
        log.info("SERVICE: Send review with ID = {}.", id);
        return review;
    }

    public Review updateReview(Review review) {
        updatedReviewValidation(review);
        Review updatedReview = reviewDbRepository.updateReview(review);
        log.info("SERVICE: Update review with ID = {}.", review.getId());

        eventDbRepository.addEvent(updatedReview.getUserId(),
                updatedReview.getId(),
                EventType.REVIEW,
                EventOperation.UPDATE);
        return updatedReview;
    }

    public void removeReviewById(long id) {
        Review removedReview = reviewDbRepository.removeReviewById(id);
        log.info("SERVICE: Delete review with ID = {}.", id);

        eventDbRepository.addEvent(removedReview.getUserId(),
                removedReview.getId(),
                EventType.REVIEW,
                EventOperation.REMOVE);
    }

    public Collection<Review> getAllReviews(long filmId, int numOfReviews) {
        Collection<Review> allReview = (reviewDbRepository.getAllReviews(filmId, numOfReviews)).stream()
                .sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .collect(Collectors.toList());
        log.info("SERVICE: Send data of {} reviews.", allReview.size());
        return allReview;
    }

    public void setLikeToReview(long reviewID, long userId) {
        filmIdValidation(reviewID);
        userValidation(userId);

        reviewDbRepository.addLike(reviewID, userId);
        log.info("SERVICE: User with ID = {} like review with ID = {}.", userId, reviewID);
    }

    public void setDislikeToReview(long reviewID, long userId) {
        filmIdValidation(reviewID);
        userValidation(userId);

        reviewDbRepository.addDislike(reviewID, userId);
        log.info("SERVICE: User with ID = {} dislike review with ID = {}.", userId, reviewID);
    }

    public void removeReviewLike(long reviewID, long userId) {
        filmIdValidation(reviewID);
        userValidation(userId);

        reviewDbRepository.removeLike(reviewID, userId);
        log.info("SERVICE: Remove like: user with ID = {} and review with ID = {}.", userId, reviewID);
    }

    public void removeReviewDislike(long reviewID, long userId) {
        filmIdValidation(reviewID);
        userValidation(userId);

        reviewDbRepository.removeDislike(reviewID, userId);
        log.info("SERVICE: Remove dislike: user with ID = {} and review with ID = {}.", userId, reviewID);
    }

    private void newReviewBaseValidation(Review review) throws ValidationException {
        long filmId = review.getFilmId();
        long userId = review.getUserId();
        String content = review.getContent();

        if(review.getIsPositive() == null) {
            log.info("VALIDATION: Incorrect positivity of review.");
            throw new ValidationException("Incorrect positivity of review");
        }

        if(content == null) {
            log.info("VALIDATION: Content can't be empty.");
            throw new ValidationException("Content can't be empty");
        }

        filmIdValidation(filmId);
        userValidation(userId);
    }

    private void updatedReviewValidation(Review review) {
        long reviewId = review.getId();
        long filmId = review.getFilmId();
        Long userId = review.getUserId();

        reviewIdValidation(reviewId);
        filmIdValidation(filmId);
        userValidation(userId);
    }

    private void userValidation(long id) {
        if (id < 1) {
            throw new NotFoundException("Incorrect user id:" + id);
        }

        try {
            userDbRepository.getById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Not found user with id:" + id);
        }
    }

    private void filmIdValidation(long id) {
        if(id == 0) {
            throw new ValidationException("Incorrect film id:0");
        }

        if (id < 0) {
            throw new NotFoundException("Incorrect film id:" + id);
        }

        try {
            filmDbRepository.getById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Not found film with id:" + id);
        }
    }

    private void reviewIdValidation(long id) {
        if (id < 1) {
            throw new NotFoundException("Incorrect review id:" + id);
        }
    }
}
