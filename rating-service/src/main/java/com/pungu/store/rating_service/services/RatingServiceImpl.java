package com.pungu.store.rating_service.services;

import com.pungu.store.rating_service.clients.UserClient;
import com.pungu.store.rating_service.dtos.RatingRequest;
import com.pungu.store.rating_service.dtos.RatingResponse;
import com.pungu.store.rating_service.dtos.SliceResponse;
import com.pungu.store.rating_service.entities.Rating;
import com.pungu.store.rating_service.repositories.RatingRepository;
import com.pungu.store.rating_service.utilities.SliceResponseUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Concrete implementation of {@link RatingService} that provides operations to create,
 * update and query {@link Rating} entities.
 *
 * <p>This service delegates persistence to {@link RatingRepository} and resolves user-related
 * information (username) through {@link UserClient}.</p>
 *
 * <p>Concurrency note: {@link #addOrUpdateRating(RatingRequest)} implements a
 * create-or-update pattern and handles the case where two concurrent requests race to create
 * the same (bookId, userId) rating by catching a {@code DataIntegrityViolationException}
 * and retrying the update path.</p>
 *
 * @author YourName
 * @since 1.0
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;
    private final UserClient userClient;

    /**
     * Adds a new rating for the specified book by the specified user, or updates the existing
     * rating if one already exists.
     *
     * <p>This method is transactional. If a rating for the same (bookId, userId) already
     * exists in the database, the existing rating's value and review are updated. If no
     * rating exists, a new {@link Rating} is created. To handle concurrent creates that
     * violate the unique constraint on (bookId, userId), the method catches
     * {@code org.springframework.dao.DataIntegrityViolationException} and retries the update
     * (fetch + save) path.</p>
     *
     * @param ratingRequest the DTO containing {@code bookId}, {@code userId}, {@code rating}
     *                      value and optional {@code review} text; must not be {@code null}
     * @return a {@link RatingResponse} representing the saved or updated rating
     * @see org.springframework.dao.DataIntegrityViolationException
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
     * Retrieves a paged slice of ratings for the specified book.
     *
     * <p>Returns a {@link SliceResponse} containing mapped {@link RatingResponse} DTOs and
     * pagination metadata. Uses {@link Slice} (rather than {@code Page}) to avoid an expensive
     * total-count query when not necessary (useful for cursor-like or infinite-scroll UIs).</p>
     *
     * @param bookId   the ID of the book whose ratings should be fetched
     * @param pageable {@link Pageable} controlling pagination and optional sorting; must not be {@code null}
     * @return a {@link SliceResponse} of {@link RatingResponse} DTOs for the requested page
     */
    @Override
    public SliceResponse<RatingResponse> getRatingsByBook(Long bookId, Pageable pageable) {
        Slice<Rating> ratingList = ratingRepository.findByBookId(bookId, pageable);
        List<RatingResponse> content = ratingList.stream()
                .map(this::toResponse)
                .toList();
        return SliceResponseUtil.mapToSLiceResponse(content, ratingList);
    }

    /**
     * Retrieves a paged slice of ratings submitted by the specified user.
     *
     * @param userId   the ID of the user whose ratings should be fetched
     * @param pageable {@link Pageable} controlling pagination and optional sorting; must not be {@code null}
     * @return a {@link SliceResponse} of {@link RatingResponse} DTOs for the requested page
     */
    @Override
    public SliceResponse<RatingResponse> getRatingsByUser(Long userId, Pageable pageable) {
        Slice<Rating> ratingList = ratingRepository.findByUserId(userId, pageable);
        List<RatingResponse> content = ratingList.stream()
                .map(this::toResponse)
                .toList();
        return SliceResponseUtil.mapToSLiceResponse(content, ratingList);
    }

    /**
     * Retrieves the rating submitted by a specific user for a specific book.
     *
     * @param bookId the ID of the book
     * @param userId the ID of the user
     * @return a {@link RatingResponse} for the matching rating, or {@code null} if no rating exists
     */
    @Override
    public RatingResponse getRatingByBookAndUser(Long bookId, Long userId) {
        return ratingRepository.findByBookIdAndUserId(bookId, userId).map(this::toResponse).orElse(null);
    }

    /**
     * Calculates and returns the average rating value for the specified book.
     *
     * <p>Delegates to {@link RatingRepository#averageForBook(Long)} which should return {@code 0.0}
     * (or an appropriate neutral value) when no ratings exist for the book. Confirm repository
     * behavior if a different fallback value is desired.</p>
     *
     * @param bookId the ID of the book
     * @return the average rating as a {@code double}; repository-specific fallback (e.g. {@code 0.0})
     *         is returned when there are no ratings
     */
    @Override
    public double getAverageRating(Long bookId) {
        return ratingRepository.averageForBook(bookId);
    }

    /**
     * Maps a {@link Rating} entity to its {@link RatingResponse} DTO.
     *
     * <p>This method resolves the reviewer's username by calling {@link UserClient#getUserNameById(Long)}.
     * Note that the user client call may propagate runtime exceptions (e.g., if the user service is
     * unavailable or returns an error), which callers should be prepared to handle.</p>
     *
     * @param rating the {@link Rating} entity to map; must not be {@code null}
     * @return a populated {@link RatingResponse} DTO
     * @throws RuntimeException if {@link UserClient#getUserNameById(Long)} fails (e.g. downstream error)
     */
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
