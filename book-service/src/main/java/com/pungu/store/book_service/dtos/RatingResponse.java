package com.pungu.store.book_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO representing a single user review and rating for a book.
 * Can be used both when showing reviews for a book or listing user-specific reviews.
 *
 * Fields:
 * - bookId: ID of the book being reviewed
 * - bookTitle: (Optional) Title of the book, useful when showing user-specific reviews
 * - userId: ID of the user who submitted the review
 * - username: (Optional) Name of the user, useful for displaying on book detail pages
 * - rating: Rating score (usually between 1.0 and 5.0)
 * - review: Textual feedback or comment by the user
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingResponse {
    private Long ratingId;

    private Long bookId;

    private Long userId;

    private Double rating;

    private String review;

    private LocalDate createdAt;
}
