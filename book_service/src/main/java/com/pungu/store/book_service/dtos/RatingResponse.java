package com.pungu.store.book_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RatingResponse {

    private Long bookId;
    private String bookTitle; // Optional, useful for user profile reviews
    private Long userId;
    private String username; // Optional, useful for book reviews
    private Double rating;
    private String review;
}
