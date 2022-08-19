package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewsService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
public class ReviewsController {
    private final ReviewsService reviewsService;

    @Autowired
    ReviewsController(ReviewsService reviewsService) {
        this.reviewsService = reviewsService;
    }

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
    public void deleteReview(@PathVariable int id) {
        reviewsService.removeReviewById(id);
    }

    @GetMapping("/reviews")
    public Collection<Review> getAllReviews(@RequestParam(defaultValue = "0") int filmId,
                                            @RequestParam(defaultValue = "10") String count) {
        return reviewsService.getAllReviews(filmId, Integer.parseInt(count));
    }

    @GetMapping("/reviews/{id}")
    public Review getReviewById(@PathVariable int id) throws NotFoundException {
        return reviewsService.getReviewById(id);
    }

    @PutMapping("/reviews/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable long userId) {
        reviewsService.setLikeToReview(id, userId);
    }

    @PutMapping("/reviews/{id}/dislike/{userId}")
    public void addDislike(@PathVariable int id, @PathVariable long userId) {
        reviewsService.setDislikeToReview(id, userId);
    }

    @DeleteMapping("/reviews/{id}/like/{userId}")
    public void removeLike(@PathVariable int id, @PathVariable long userId) {
        reviewsService.removeReviewLike(id, userId);
    }

    @DeleteMapping("/reviews/{id}/dislike/{userId}")
    public void removeDislike(@PathVariable int id, @PathVariable long userId) {
        reviewsService.removeReviewDislike(id, userId);
    }

}
