package com.pungu.store.book_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class BookResponse {
    private String title;
    private String authorName;
    private String description;
    private String coverImage;
    private LocalDate publicationYear;
    private Double averageRating; // New Field
    private List<RatingResponse> reviews; // New Field
    private boolean availableForReading;
    private boolean availableForDownload;
}
