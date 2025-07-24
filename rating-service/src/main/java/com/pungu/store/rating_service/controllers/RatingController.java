package com.pungu.store.rating_service.controllers;

import com.pungu.store.rating_service.entities.Rating;
import com.pungu.store.rating_service.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing book ratings.
 * Provides endpoints to add/update ratings, retrieve ratings by book or user,
 * and get the average rating of a book.
 */
@RestController
@RequestMapping("/api/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    /**
     * Adds a new rating or updates an existing one for a given user and book.
     *
     * @param rating the rating object with bookId, userId, rating value, and comment
     * @return the saved or updated rating
     */
    @PostMapping
    public ResponseEntity<Rating> addOrUpdateRating(@RequestBody Rating rating) {
        return ResponseEntity.ok(ratingService.addRating(rating));
    }

    /**
     * Retrieves all ratings for a specific book.
     *
     * @param bookId the ID of the book
     * @return list of ratings for the given book
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<Rating>> getRatingsByBook(@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(ratingService.getRatingsByBook(bookId));
    }

    /**
     * Retrieves all ratings submitted by a specific user.
     *
     * @param userId the ID of the user
     * @return list of ratings submitted by the user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Rating>> getRatingsByUser(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(ratingService.getRatingsByUser(userId));
    }

    /**
     * Calculates the average rating for a specific book.
     *
     * @param bookId the ID of the book
     * @return average rating of the book
     */
    @GetMapping("/book/{bookId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable("bookId") Long bookId) {
        return ResponseEntity.ok(ratingService.getAverageRating(bookId));
    }
}