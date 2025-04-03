package com.pungu.store.book_service.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String authorName;
    private String description;
    @NotBlank
    private String genre;
    private String fileUrl;
    private String coverImageUrl;
    private LocalDate publicationYear;
}
