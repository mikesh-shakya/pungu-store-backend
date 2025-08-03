package com.pungu.store.book_service.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorClientFallback implements AuthorClient {

    @Override
    public Long getAuthorIdByName(String authorName) {
        log.warn("Fallback triggered: Unable to fetch author ID for name '{}'", authorName);
        // Returning null explicitly so the caller can distinguish fallback behavior
        return null;
    }

    @Override
    public String getAuthorNameById(Long authorId) {
        log.warn("Fallback triggered: Unable to fetch author name for ID {}", authorId);
        return "Unknown Author";
    }
}
