package com.pungu.store.book_service.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorClientFallback implements AuthorClient {
    @Override
    public String getAuthorNameById(Long authorId) {
        log.warn("Fallback triggered: Unable to fetch author name for ID {}", authorId);
        return "Unknown Author";
    }
}
