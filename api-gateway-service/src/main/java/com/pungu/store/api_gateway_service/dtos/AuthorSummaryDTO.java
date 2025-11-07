package com.pungu.store.api_gateway_service.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthorSummaryDTO(Long authorId,
                               String fullName,
                               String message) {
    public static AuthorSummaryDTO unavailable(Long authorId) {
        return new AuthorSummaryDTO(authorId, null, "Author information unavailable");
    }
}