package com.pungu.store.book_service.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO used to receive book creation or update requests from clients.
 * It contains basic information about the book, and a reference to the author via authorId.
 *
 * Fields:
 * - title: Book title (must not be blank)
 * - authorId: Foreign key reference to the Author
 * - genre: Genre of the book (must not be blank)
 * - language: The language of the book
 * - description: Optional description of the book
 * - coverImageUrl: Optional URL for the book's cover image
 * - publicationYear: Optional publication date (just year may be acceptable)
 */
@Data
public class BookRequest {

    @NotBlank(message = "Title cannot be blank")
    private String title;
    private Long authorId;
    @NotBlank(message = "Genre cannot be blank")
    private String genre;
    private String language;
    private String description;
    private String coverImageUrl;
    private LocalDate publicationDate;
}
