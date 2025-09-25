package com.pungu.store.rating_service.repositories;

import com.pungu.store.rating_service.entities.Rating;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Rating} entities.
 */
@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    /**
     * Return the average rating of a specific book
     * @param bookId ID of the book
     * @return average rating of the book
     */
    @Query("select coalesce(avg(r.rating), 0) from Rating r where r.bookId = :bookId")
    double averageForBook(@Param("bookId") Long bookId);

    /**
     * Retrieves all ratings for a specific book.
     *
     * @param bookId ID of the book
     * @return list of ratings for the book
     */
    Slice<Rating> findByBookId(Long bookId, Pageable pageable);

    /**
     * Retrieves all ratings submitted by a specific user.
     *
     * @param userId ID of the user
     * @return list of ratings given by the user
     */
    Slice<Rating> findByUserId(Long userId, Pageable pageable);

    /**
     * Retrieves a rating submitted by a specific user for a specific book.
     *
     * @param bookId ID of the book
     * @param userId ID of the user
     * @return optional containing the rating if exists
     */
    Optional<Rating> findByBookIdAndUserId(Long bookId, Long userId);
}