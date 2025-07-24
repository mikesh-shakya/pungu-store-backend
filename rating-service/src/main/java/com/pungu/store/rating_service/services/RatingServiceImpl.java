package com.pungu.store.rating_service.services;

import com.pungu.store.rating_service.entities.Rating;
import com.pungu.store.rating_service.repositories.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the RatingService interface.
 * Provides functionality to manage and query book ratings.
 */
@RequiredArgsConstructor
@Service
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;

    /**
     * Adds a new rating for a book by a user, or updates the existing one if already present.
     *
     * <p>If the user has already rated the book, this method will update the rating value and comment.
     * Otherwise, it will create a new rating entry.</p>
     *
     * @param rating the Rating object containing bookId, userId, rating value, and optional comment
     * @return the saved or updated Rating object
     */
    @Override
    public Rating addRating(Rating rating) {
        Optional<Rating> existingRating = ratingRepository.findByBookIdAndUserId(rating.getBookId(), rating.getUserId());

        if (existingRating.isPresent()) {
            Rating oldRating = existingRating.get();
            oldRating.setRating(rating.getRating());
            return ratingRepository.save(oldRating);
        }

        return ratingRepository.save(rating);
    }

    /**
     * Retrieves all ratings for a given book.
     *
     * @param bookId the ID of the book
     * @return a list of ratings for the book
     */
    @Override
    public List<Rating> getRatingsByBook(Long bookId) {
        return ratingRepository.findByBookId(bookId);
    }

    /**
     * Retrieves all ratings submitted by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of ratings submitted by the user
     */
    @Override
    public List<Rating> getRatingsByUser(Long userId) {
        return ratingRepository.findByUserId(userId);
    }

    /**
     * Retrieves the rating for a specific book submitted by a specific user.
     *
     * @param bookId the ID of the book
     * @param userId the ID of the user
     * @return the matching Rating object, or null if none found
     */
    @Override
    public Rating getRatingByBookAndUser(Long bookId, Long userId) {
        return ratingRepository.findByBookIdAndUserId(bookId, userId).orElse(null);
    }

    /**
     * Calculates the average rating for a specific book.
     *
     * @param bookId the ID of the book
     * @return the average rating value, or 0.0 if no ratings are found
     */
    @Override
    public double getAverageRating(Long bookId) {
        List<Rating> ratings = ratingRepository.findByBookId(bookId);
        if (ratings.isEmpty()) return 0.0;
        return ratings.stream().mapToInt(Rating::getRating).average().orElse(0.0);
    }
}