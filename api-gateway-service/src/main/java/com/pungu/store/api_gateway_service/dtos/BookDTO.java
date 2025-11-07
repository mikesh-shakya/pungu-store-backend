package com.pungu.store.api_gateway_service.dtos;

import java.time.LocalDate;

public record BookDTO(Long bookId,
                      String title,
                      Long authorId,
                      String description,
                      String genre,
                      String language,
                      LocalDate publicationDate,
                      String coverImageUrl) {
}