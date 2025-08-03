package com.pungu.store.book_service.clients;

import com.pungu.store.book_service.dtos.RatingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class RatingReviewClientFallback implements RatingReviewClient {

    @Override
    public List<RatingResponse> getRatingsForBook(Long bookId) {
        log.warn("Fallback triggered: Unable to fetch ratings for book ID {}", bookId);
        return Collections.emptyList(); // Return empty list to avoid null pointer issues
    }
}
