package com.pungu.store.book_service.clients;

import com.pungu.store.book_service.dtos.RatingResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Feign client to communicate with the Rating & Review Service.
 * Used to fetch all ratings and reviews for a specific book.
 */
@FeignClient(name = "rating-service", fallback = RatingReviewClientFallback.class) // Uses Eureka for service discovery
public interface RatingReviewClient {

    /**
     * Fetches all ratings and reviews associated with a given book ID.
     *
     * @param bookId ID of the book
     * @return List of RatingResponse DTOs containing rating and review details
     */
    @GetMapping("/api/ratings/book/{bookId}")
    List<RatingResponse> getRatingsForBook(@PathVariable("bookId") Long bookId);

}
