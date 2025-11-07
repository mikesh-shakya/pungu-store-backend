package com.pungu.store.api_gateway_service.dtos;// ReviewWithUserAndBookDTO.java

import java.time.LocalDateTime;

public record ReviewWithUserAndBookDTO(Long ratingId,
                                       BookSummaryDTO book,
                                       Long userId,
                                       String userName,
                                       Integer rating,
                                       String review,
                                       LocalDateTime createdAt,
                                       LocalDateTime lastUpdatedAt) {
}
