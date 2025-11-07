package com.pungu.store.api_gateway_service.services;

import com.pungu.store.api_gateway_service.dtos.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Aggregator service that merges review data (rating-service)
 * with user data (auth-service) and book data (book-service).
 * Supports pagination and per-request caching for performance.
 */
@Service
@Slf4j
public class ReviewAggregatorService {

    private final WebClient ratingWebClient;
    private final WebClient userWebClient;
    private final WebClient bookWebClient;

    // Caches for user and book lookups (reset per request)
    private final Map<Long, Mono<String>> userCache = new ConcurrentHashMap<>();
    private final Map<Long, Mono<BookSummaryDTO>> bookCache = new ConcurrentHashMap<>();

    public ReviewAggregatorService(
            @Qualifier("ratingWebClient") WebClient ratingWebClient,
            @Qualifier("userWebClient") WebClient userWebClient,
            @Qualifier("bookWebClient") WebClient bookWebClient
    ) {
        this.ratingWebClient = ratingWebClient;
        this.userWebClient = userWebClient;
        this.bookWebClient = bookWebClient;
    }

    /**
     * Fetches all reviews for a given book (or user), enriches with user and book info.
     * Handles caching of repeated userId/bookId lookups.
     */
    public Mono<SliceResponse<ReviewWithUserAndBookDTO>> getAllReviewsWithDetails(
            Long id, int offset, int limit, String orderBy, boolean isBookBased) {
        log.info("Fetching reviews for book with ID: {}", id);

        // reset caches for each new request
        userCache.clear();
        bookCache.clear();

        String targetPath = isBookBased ? "/book/{id}" : "/user/{id}";
        String targetType = isBookBased ? "book" : "user";

        log.info("Fetching reviews for {} with ID: {}", targetType, id);

        ParameterizedTypeReference<SliceResponse<ReviewDTO>> typeRef =
                new ParameterizedTypeReference<>() {
                };

        return ratingWebClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path(targetPath)
                            .queryParam("offset", offset)
                            .queryParam("limit", limit);
                    if (orderBy != null) builder.queryParam("orderBy", orderBy);
                    return builder.build(id);
                })
                .retrieve()
                .bodyToMono(typeRef)
                .flatMap(sliceResponse -> {
                    List<ReviewDTO> reviews = sliceResponse.items();
                    log.info("Fetched {} reviews for book {}", reviews.size(), id);

                    return Flux.fromIterable(reviews)
                            .flatMap(this::enrichWithUserAndBook)
                            .collectList()
                            .map(enriched -> new SliceResponse<>(
                                    enriched,
                                    sliceResponse.currentPageNumber(),
                                    sliceResponse.numberOfElements(),
                                    sliceResponse.pageSize(),
                                    sliceResponse.hasMore()
                            ));
                })
                .doOnError(error -> log.error("Error fetching or enriching reviews for book {}: {}",
                        id, error.getMessage()));
    }

    /**
     * Enriches review with userName and bookTitle using cached WebClient calls.
     */
    private Mono<ReviewWithUserAndBookDTO> enrichWithUserAndBook(ReviewDTO review) {
        Mono<String> userNameMono = fetchUserName(review.userId());
        Mono<BookSummaryDTO> bookTitleMono = fetchBookTitle(review.bookId());

        return Mono.zip(userNameMono, bookTitleMono)
                .map(tuple -> new ReviewWithUserAndBookDTO(
                        review.ratingId(),
                        tuple.getT2(),  // bookSummaryDTO
                        review.userId(),
                        tuple.getT1(),  // userName
                        review.rating(),
                        review.review(),
                        review.createdAt(),
                        review.lastUpdatedAt()
                ));
    }

    /**
     * Fetches username from cache or auth-service.
     */
    private Mono<String> fetchUserName(Long userId) {
        if (userId == null) return Mono.just("unknown-user");

        return userCache.computeIfAbsent(userId, id ->
                userWebClient.get()
                        .uri("/{userId}", id)
                        .retrieve()
                        .bodyToMono(UserSummaryDTO.class)
                        .map(user -> (user.firstName() + " " + user.lastName()).trim())
                        .doOnNext(name -> log.debug("Fetched user {} from auth-service", id))
                        .onErrorResume(WebClientResponseException.class, ex -> {
                            log.warn("User lookup failed for userId={} with status {}: {}",
                                    id, ex.getStatusCode(), ex.getMessage());
                            return Mono.just("unknown-user");
                        })
                        .onErrorResume(Exception.class, ex -> {
                            log.error("Unexpected error fetching user {}: {}", id, ex.getMessage());
                            return Mono.just("unknown-user");
                        })
                        .cache()
        );
    }

    /**
     * Fetches book title from cache or book-service.
     */
    private Mono<BookSummaryDTO> fetchBookTitle(Long bookId) {
        if (bookId == null) return Mono.just(new BookSummaryDTO(null, "unknown-book"));

        return bookCache.computeIfAbsent(bookId, id ->
                bookWebClient.get()
                        .uri("/{bookId}", id)
                        .retrieve()
                        .bodyToMono(BookSummaryDTO.class)
                        .doOnNext(book -> log.debug("Fetched book {} from book-service", id))
                        .onErrorResume(WebClientResponseException.class, ex -> {
                            log.warn("Book lookup failed for bookId={} with status {}: {}",
                                    id, ex.getStatusCode(), ex.getMessage());
                            return Mono.just(new BookSummaryDTO(id, "unknown-book"));
                        })
                        .onErrorResume(Exception.class, ex -> {
                            log.error("Unexpected error fetching book {}: {}", id, ex.getMessage());
                            return Mono.just(new BookSummaryDTO(id, "unknown-book"));
                        })
                        .cache()
        );
    }
}
