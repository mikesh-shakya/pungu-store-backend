package com.pungu.store.api_gateway_service.services;

import com.pungu.store.api_gateway_service.dtos.AuthorSummaryDTO;
import com.pungu.store.api_gateway_service.dtos.BookDTO;
import com.pungu.store.api_gateway_service.dtos.BookWithAuthorDTO;
import com.pungu.store.api_gateway_service.dtos.SliceResponse;
import com.pungu.store.api_gateway_service.exceptions.BookNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class BookAggregatorService {

    private final WebClient authorWebClient;
    private final WebClient bookWebClient;

    // Caches for author and book lookups (reset per request)
    private final Map<Long, Mono<AuthorSummaryDTO>> authorCache = new ConcurrentHashMap<>();
    private final Map<Long, Mono<String>> bookCache = new ConcurrentHashMap<>();

    public BookAggregatorService(
            @Qualifier("authorWebClient") WebClient authorWebClient,
            @Qualifier("bookWebClient") WebClient bookWebClient
    ) {
        this.authorWebClient = authorWebClient;
        this.bookWebClient = bookWebClient;
    }


    public Mono<BookWithAuthorDTO> getBookWithDetails(Long id, int offset, int limit, String orderBy) {
        log.info("Fetching book with ID: {}", id);

        return bookWebClient.get()
                .uri("/{bookId}", id)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response -> {
                    log.warn("Book not found for ID {}", id);
                    return Mono.error(new BookNotFoundException(id));
                })
                .bodyToMono(BookDTO.class)
                .doOnNext(book -> log.debug("Fetched book {} from book-service", id))
                .flatMap(this::enrichWithAuthor)
                .onErrorResume(BookNotFoundException.class, Mono::error) // just propagate
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("Error fetching book {}: {}", id, ex.getMessage());
                    return Mono.error(ex);
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Unexpected error fetching book {}: {}", id, ex.getMessage());
                    return Mono.error(ex);
                });
    }


    public Mono<SliceResponse<BookWithAuthorDTO>> getAllBooksWithDetails(int offset, int limit, String orderBy) {
        log.info("Fetching all books.");
        // reset caches for each new request
        authorCache.clear();
        bookCache.clear();

        ParameterizedTypeReference<SliceResponse<BookDTO>> typeRef = new ParameterizedTypeReference<>() {
        };

        return bookWebClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .queryParam("offset", offset)
                            .queryParam("limit", limit);
                    if (orderBy != null) builder.queryParam("orderBy", orderBy);
                    return builder.build();
                })
                .retrieve()
                .bodyToMono(typeRef)
                .flatMap(sliceResponse -> {
                    List<BookDTO> books = sliceResponse.items();
                    log.info("Fetched {} book", books.size());

                    return Flux.fromIterable(books)
                            .flatMap(this::enrichWithAuthor)
                            .collectList()
                            .map(enriched -> new SliceResponse<>(
                                    enriched,
                                    sliceResponse.currentPageNumber(),
                                    sliceResponse.numberOfElements(),
                                    sliceResponse.pageSize(),
                                    sliceResponse.hasMore()
                            ));
                })
                .doOnError(error -> log.error("Error fetching or enriching books: {}", error.getMessage()));
    }


    private Mono<BookWithAuthorDTO> enrichWithAuthor(BookDTO book) {
        return fetchAuthorDetails(book.bookId())
                .map(author -> new BookWithAuthorDTO(
                        book.bookId(),
                        book.title(),
                        author,  // AuthorSummaryDTO
                        book.description(),
                        book.genre(),
                        book.language(),
                        book.publicationDate(),
                        book.coverImageUrl()
                ));
    }


    /**
     * Fetches author details from cache or author-service.
     */
    private Mono<AuthorSummaryDTO> fetchAuthorDetails(Long authorId) {
        if (authorId == null) {
            return Mono.just(AuthorSummaryDTO.unavailable(null));
        }

        return authorCache.computeIfAbsent(authorId, id ->
                authorWebClient.get()
                        .uri("/{authorId}", id)
                        .retrieve()
                        .bodyToMono(AuthorSummaryDTO.class)
                        .doOnNext(author -> log.debug("Fetched author {} from author-service", id))
                        .onErrorResume(WebClientResponseException.class, ex -> {
                            log.warn("Author lookup failed for authorId={} with status {}: {}",
                                    id, ex.getStatusCode(), ex.getMessage());
                            return Mono.just(AuthorSummaryDTO.unavailable(id));
                        })
                        .onErrorResume(Exception.class, ex -> {
                            log.error("Unexpected error fetching author {}: {}", id, ex.getMessage());
                            return Mono.just(AuthorSummaryDTO.unavailable(id));
                        })
                        .cache()
        );
    }


}
