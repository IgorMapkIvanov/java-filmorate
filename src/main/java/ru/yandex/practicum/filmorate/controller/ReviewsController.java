package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewsService;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@AllArgsConstructor
public class ReviewsController {
    private final ReviewsService reviewsService;

    @PostMapping("/reviews")
    public Review addReview(@Valid @RequestBody Review review) {
        System.out.println(review);
        return reviewsService.addReview(review);
    }

    @PutMapping("/reviews")
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewsService.updateReview(review);
    }

    @DeleteMapping("/reviews/{id}")
    public void deleteReview(@PathVariable long id) {
        reviewsService.removeReviewById(id);
    }

    @GetMapping("/reviews")
    public Collection<Review> getAllReviews(@RequestParam(defaultValue = "0") Long filmId,
                                            @RequestParam(defaultValue = "10") String count) {
        return reviewsService.getAllReviews(filmId, Integer.parseInt(count));
    }

    @GetMapping("/reviews/{id}")
    public Review getReviewById(@PathVariable long id) throws NotFoundException {
        return reviewsService.getReviewById(id);
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        reviewsService.setLikeToReview(id, userId);
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public void addDislike(@PathVariable long id, @PathVariable long userId) {
        reviewsService.setDislikeToReview(id, userId);
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        reviewsService.removeReviewLike(id, userId);
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable long id, @PathVariable long userId) {
        reviewsService.removeReviewDislike(id, userId);
    }

}
