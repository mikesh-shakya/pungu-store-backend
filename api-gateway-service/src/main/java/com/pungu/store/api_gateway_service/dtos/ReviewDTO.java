package com.pungu.store.api_gateway_service.dtos;

import java.time.LocalDateTime;

public record ReviewDTO(Long ratingId, Long bookId, Long userId, int rating, String review, LocalDateTime createdAt,
                        LocalDateTime lastUpdatedAt) {
}
