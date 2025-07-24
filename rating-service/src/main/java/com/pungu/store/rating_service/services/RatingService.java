package com.pungu.store.rating_service.services;

import com.pungu.store.rating_service.entities.Rating;

import java.util.List;

/**
 * Service interface for managing book ratings and reviews.
 */
public interface RatingService {

    /**
     * Adds a new rating and review for a book by a user.
     *
     * @param rating the rating entity to be added
     * @return the saved rating entity
     */
    Rating addRating(Rating rating);

    /**
     * Retrieves all ratings for a specific book.
     *
     * @param bookId ID of the book
     * @return list of ratings for the given book
     */
    List<Rating> getRatingsByBook(Long bookId);

    /**
     * Retrieves all ratings submitted by a specific user.
     *
     * @param userId ID of the user
     * @return list of ratings given by the user
     */
    List<Rating> getRatingsByUser(Long userId);

    /**
     * Calculates the average rating of a specific book.
     *
     * @param bookId ID of the book
     * @return average rating value between 1.0 and 5.0
     */
    double getAverageRating(Long bookId);
    /**
     * Retrieves the rating submitted by a user for a specific book, if it exists.
     *
     * @param bookId ID of the book
     * @param userId ID of the user
     * @return rating object or null if not found
     */
    Rating getRatingByBookAndUser(Long bookId, Long userId);
}
