package com.pungu.store.rating_service.dtos;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RatingResponse {
    private Long ratingId;
    private Long bookId;
    private Long userId;
    private String userName;
    private int rating;
    private String review;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
}

