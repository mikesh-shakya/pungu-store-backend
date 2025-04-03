package com.pungu.store.rating_service.controllers;

import com.pungu.store.rating_service.entities.Rating;
import com.pungu.store.rating_service.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    // Add a new rating
    @PostMapping
    public ResponseEntity<Rating> addRating(@RequestBody Rating rating) {
        return ResponseEntity.ok(ratingService.addRating(rating));
    }

    // Get all ratings for a specific book
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Rating>> getRatingsByBook(@PathVariable Long bookId) {
        return ResponseEntity.ok(ratingService.getRatingsByBook(bookId));
    }

    // Get all ratings given by a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Rating>> getRatingsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ratingService.getRatingsByUser(userId));
    }

    // Get the average rating for a book
    @GetMapping("/book/{bookId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long bookId) {
        return ResponseEntity.ok(ratingService.getAverageRating(bookId));
    }
}
