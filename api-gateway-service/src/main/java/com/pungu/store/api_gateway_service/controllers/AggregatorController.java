package com.pungu.store.api_gateway_service.controllers;

import com.pungu.store.api_gateway_service.dtos.BookWithAuthorDTO;
import com.pungu.store.api_gateway_service.dtos.ReviewWithUserAndBookDTO;
import com.pungu.store.api_gateway_service.dtos.SliceResponse;
import com.pungu.store.api_gateway_service.services.BookAggregatorService;
import com.pungu.store.api_gateway_service.services.ReviewAggregatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/aggregated")
@RequiredArgsConstructor
public class AggregatorController {

    private final ReviewAggregatorService reviewAggregatorService;
    private final BookAggregatorService bookAggregatorService;

    @GetMapping("/reviews/book/{bookId}")
    public Mono<SliceResponse<ReviewWithUserAndBookDTO>> getAggregatedBookReviews(@PathVariable("bookId") Long bookId,
                                                                                  @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                                                  @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                                                  @RequestParam(value = "orderBy", required = false) String orderBy) {
        return reviewAggregatorService.getAllReviewsWithDetails(bookId, offset, limit, orderBy, true);
    }

    @GetMapping("/reviews/user/{userId}")
    public Mono<SliceResponse<ReviewWithUserAndBookDTO>> getAggregatedUserReviews(@PathVariable("userId") Long userId,
                                                                                  @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                                                  @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                                                  @RequestParam(value = "orderBy", required = false) String orderBy) {
        return reviewAggregatorService.getAllReviewsWithDetails(userId, offset, limit, orderBy, false);
    }


    @GetMapping("/books")
    public Mono<SliceResponse<BookWithAuthorDTO>> getAggregatedBooks(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                                     @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                                     @RequestParam(value = "orderBy", required = false) String orderBy) {
        return bookAggregatorService.getAllBooksWithDetails(offset, limit, orderBy);
    }

    @GetMapping("/books/{bookId}")
    public Mono<BookWithAuthorDTO> getAggregatedBook(@PathVariable("bookId") Long bookId,
                                                                             @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                                             @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                                             @RequestParam(value = "orderBy", required = false) String orderBy) {
        return bookAggregatorService.getBookWithDetails(bookId, offset, limit, orderBy);
    }
}
