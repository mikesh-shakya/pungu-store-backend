package com.pungu.store.book_service.dtos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
/**
 * DTO used to send detailed book information to the client.
 * Includes metadata about the book, author, reviews, and availability.
 * Fields:
 * - bookId: The id of the book
 * - title: The title of the book
 * - authorId: The id of the author
 * - authorName: The name of the author
 * - description: A short summary or synopsis of the book
 * - genre: The genre of the book
 * - language: The language of the book
 * - coverImage: URL to the cover image of the book
 * - publicationYear: The year the book was published
 */
@Data
@Builder
public class BookResponse {

    private long bookId;
    private String title;
    private Long authorId;
    private String description;
    private String genre;
    private String language;
    private LocalDate publicationDate;
    private String coverImageUrl;
}
