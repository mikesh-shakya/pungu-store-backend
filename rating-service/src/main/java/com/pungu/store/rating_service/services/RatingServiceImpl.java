package com.pungu.store.rating_service.services;

import com.pungu.store.rating_service.clients.UserClient;
import com.pungu.store.rating_service.dtos.RatingRequest;
import com.pungu.store.rating_service.dtos.RatingResponse;
import com.pungu.store.rating_service.entities.Rating;
import com.pungu.store.rating_service.repositories.RatingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the RatingService interface.
 * Provides functionality to manage and query book ratings.
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final UserClient userClient;

    /**
     * Adds a new rating for a book by a user, or updates the existing one if already present.
     *
     * <p>If the user has already rated the book, this method will update the rating value and comment.
     * Otherwise, it will create a new rating entry.</p>
     *
     * @param ratingRequest the CreateRatingRequest object containing bookId, userId, rating value, and optional comment
     * @return the saved or updated RatingResponse object
     */
    @Override
    @Transactional
    public RatingResponse addOrUpdateRating(RatingRequest ratingRequest) {
        Optional<Rating> existingRating = ratingRepository.findByBookIdAndUserId(ratingRequest.getBookId(), ratingRequest.getUserId());

        if (existingRating.isPresent()) {
            log.info("The rating already exists for this book {} by this user with user id {}.", ratingRequest.getBookId(), ratingRequest.getUserId());
            Rating oldRating = existingRating.get();
            oldRating.setRating(ratingRequest.getRating());
            oldRating.setReview(ratingRequest.getReview());
            return toResponse(ratingRepository.save(oldRating));
        }

        // Create new rating
        log.info("There is no rating for this book {} by this user with user id {}.", ratingRequest.getBookId(), ratingRequest.getUserId());
        log.info("Creating a new rating...");
        Rating newReview = Rating.builder()
                .bookId(ratingRequest.getBookId())
                .userId(ratingRequest.getUserId())
                .rating(ratingRequest.getRating())
                .review(ratingRequest.getReview())
                .build();

        // If two requests race to create, the unique constraint (bookId,userId) can throw
        // a DataIntegrityViolationException. Catch and retry update path.
        try {
            return toResponse(ratingRepository.save(newReview));
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            Rating old = ratingRepository.findByBookIdAndUserId(
                    ratingRequest.getBookId(), ratingRequest.getUserId()).orElseThrow();
            old.setRating(ratingRequest.getRating());
            old.setReview(ratingRequest.getReview());
            return toResponse(ratingRepository.save(old));
        }
    }

    /**
     * Retrieves all ratings for a given book.
     *
     * @param bookId the ID of the book
     * @return a list of ratings for the book
     */
    @Override
    public List<RatingResponse> getRatingsByBook(Long bookId) {
        return ratingRepository.findByBookId(bookId).stream().map(this::toResponse).toList();
    }

    /**
     * Retrieves all ratings submitted by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of ratings submitted by the user
     */
    @Override
    public List<RatingResponse> getRatingsByUser(Long userId) {
        return ratingRepository.findByUserId(userId).stream().map(this::toResponse).toList();
    }

    /**
     * Retrieves the rating for a specific book submitted by a specific user.
     *
     * @param bookId the ID of the book
     * @param userId the ID of the user
     * @return the matching Rating object, or null if none found
     */
    @Override
    public RatingResponse getRatingByBookAndUser(Long bookId, Long userId) {
        return ratingRepository.findByBookIdAndUserId(bookId, userId).map(this::toResponse).orElse(null);
    }

    /**
     * Calculates the average rating for a specific book.
     *
     * @param bookId the ID of the book
     * @return the average rating value, or 0.0 if no ratings are found
     */
    @Override
    public double getAverageRating(Long bookId) {
        return ratingRepository.averageForBook(bookId);
    }


    public RatingResponse toResponse(Rating rating) {
        String username = userClient.getUserNameById(rating.getUserId());
        return RatingResponse.builder()
                .ratingId(rating.getRatingId())
                .bookId(rating.getBookId())
                .userId(rating.getUserId())
                .userName(username)
                .rating(rating.getRating())
                .review(rating.getReview())
                .createdAt(rating.getCreatedAt())
                .lastUpdatedAt(rating.getLastUpdatedAt())
                .build();
    }
}