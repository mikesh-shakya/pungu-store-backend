package com.pungu.store.rating_service.services;

import com.pungu.store.rating_service.dtos.RatingRequest;
import com.pungu.store.rating_service.dtos.RatingResponse;
import com.pungu.store.rating_service.dtos.SliceResponse;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing book ratings and reviews.
 *
 * <p>Provides operations to create or update ratings, query ratings by book or user,
 * retrieve individual ratings, and calculate average ratings for books.</p>
 */
public interface RatingService {

    /**
     * Adds a new rating and review for a book by a user, or updates the existing one
     * if the user has already rated the book.
     *
     * @param rating a {@link RatingRequest} containing book ID, user ID, rating value,
     *               and optional review text
     * @return a {@link RatingResponse} representing the saved or updated rating
     */
    RatingResponse addOrUpdateRating(RatingRequest rating);

    /**
     * Retrieves a slice of ratings for the specified book.
     *
     * <p>Results are returned in a {@link SliceResponse}, which includes both
     * a list of {@link RatingResponse} DTOs and pagination metadata.
     * Unlike a {@code Page}, a {@code Slice} does not include the total count,
     * making it more efficient for infinite-scroll or cursor-based UIs.</p>
     *
     * @param bookId   the ID of the book whose ratings should be fetched
     * @param pageable pagination and sorting information
     * @return a {@link SliceResponse} of ratings for the given book
     */
    SliceResponse<RatingResponse> getRatingsByBook(Long bookId, Pageable pageable);

    /**
     * Retrieves a slice of ratings submitted by the specified user.
     *
     * @param userId   the ID of the user whose ratings should be fetched
     * @param pageable pagination and sorting information
     * @return a {@link SliceResponse} of ratings given by the user
     */
    SliceResponse<RatingResponse> getRatingsByUser(Long userId, Pageable pageable);

    /**
     * Calculates the average rating value for a specific book.
     *
     * @param bookId the ID of the book
     * @return the average rating value (typically between 1.0 and 5.0),
     *         or {@code 0.0} if no ratings exist
     */
    double getAverageRating(Long bookId);

    /**
     * Retrieves the rating submitted by a specific user for a specific book.
     *
     * @param bookId the ID of the book
     * @param userId the ID of the user
     * @return a {@link RatingResponse} for the rating, or {@code null} if not found
     */
    RatingResponse getRatingByBookAndUser(Long bookId, Long userId);
}
