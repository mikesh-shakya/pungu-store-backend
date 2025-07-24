package com.pungu.store.book_service.clients;

import org.springframework.stereotype.Component;

@Component
public class AuthorClientFallback implements AuthorClient{
    @Override
    public Long getAuthorIdByName(String authorName) {
        return 0L;
    }

    @Override
    public String getAuthorNameById(Long authorId) {
        return "";
    }
}
