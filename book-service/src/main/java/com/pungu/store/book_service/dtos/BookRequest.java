package com.pungu.store.book_service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO used to receive book creation or update requests from clients.
 * It contains basic information about the book, and a reference to the author via authorId.
 *
 * Fields:
 * - title: Book title (must not be blank)
 * - description: Optional description of the book
 * - genre: Genre of the book (must not be blank)
 * - fileUrl: Optional URL pointing to the book file
 * - coverImageUrl: Optional URL for the book's cover image
 * - publicationYear: Optional publication date (just year may be acceptable)
 * - authorId: Required foreign key reference to the Author (from Author Service)
 */
@Data
public class BookRequest {

    @NotBlank(message = "Title cannot be blank")
    private String title;

    private String description;

    @NotBlank(message = "Genre cannot be blank")
    private String genre;

    private String fileUrl;
    private String coverImageUrl;
    private LocalDate publicationYear;

    @NotNull(message = "Author ID must be provided")
    private Long authorId;
}
