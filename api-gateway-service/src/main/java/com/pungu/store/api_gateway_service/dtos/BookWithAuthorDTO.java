package com.pungu.store.api_gateway_service.dtos;

import java.time.LocalDate;

public record BookWithAuthorDTO(Long bookId,
                                String title,
                                AuthorSummaryDTO author,
                                String description,
                                String genre,
                                String language,
                                LocalDate publicationDate,
                                String coverImageUrl) {
}