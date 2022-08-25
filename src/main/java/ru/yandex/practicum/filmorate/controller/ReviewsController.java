package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewsService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping(value = "/reviews")

public class ReviewsController {
    private final ReviewsService reviewsService;

    @GetMapping
    public Collection<Review> getAllReviews(@RequestParam(defaultValue = "0") Long filmId,
                                            @RequestParam(defaultValue = "10") String count) {
        log.info("CONTROLLER: Request for list of all reviews.");
        return reviewsService.getAllReviews(filmId, Integer.parseInt(count));
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Long id) throws NotFoundException {
        log.info("CONTROLLER: Request for review with ID = {}.", id);
        return reviewsService.getReviewById(id);
    }

    @PostMapping
    public Review addReview(@Valid @RequestBody Review review) {
        log.info("CONTROLLER: Request to add new review: {}", review);
        return reviewsService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("CONTROLLER: Request to update review with ID = {}.", review.getId());
        return reviewsService.updateReview(review);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("CONTROLLER: Request user with ID = {} like review with ID = {}.", userId, id);
        reviewsService.setLikeToReview(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("CONTROLLER: Request user with ID = {} dislike review with ID = {}.", userId, id);
        reviewsService.setDislikeToReview(id, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        log.info("CONTROLLER: Request to delete review with ID = {}.", id);
        reviewsService.removeReviewById(id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("CONTROLLER: Request to delete user with ID = {} like review with ID = {}.", userId, id);
        reviewsService.removeReviewLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("CONTROLLER: Request to delete user with ID = {} dislike review with ID = {}.", userId, id);
        reviewsService.removeReviewDislike(id, userId);
    }

}
