package com.pungu.store.book_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO used to send detailed book information to the client.
 * Includes metadata about the book, author, reviews, and availability.
 *
 * Fields:
 * - title: The title of the book
 * - authorName: The name of the author (retrieved via Author Service)
 * - description: A short summary or synopsis of the book
 * - coverImage: URL to the cover image of the book
 * - publicationYear: The year the book was published
 * - averageRating: Average rating computed from all user reviews
 * - reviews: List of individual rating responses (review DTOs)
 * - availableForReading: Boolean indicating if the book is available to read online
 * - availableForDownload: Boolean indicating if the book is available for download
 */
@Data
@AllArgsConstructor
public class BookResponse {

    private String title;

    private String authorName;

    private String description;

    private String coverImageUrl;

    private LocalDate publicationYear;

    private Double averageRating;

    private List<RatingResponse> reviews;

    private boolean availableForReading;

    private boolean availableForDownload;
}
