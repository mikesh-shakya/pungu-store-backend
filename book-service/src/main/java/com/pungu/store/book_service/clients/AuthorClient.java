package com.pungu.store.book_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
/**
 * Feign client for communicating with the Author Service via service discovery (e.g., Eureka).
 * Allows Book Service to fetch author IDs and names.
 */
@FeignClient(name = "author-service", fallback = AuthorClientFallback.class) // Uses Eureka to discover 'author-service'
public interface AuthorClient {
    /**
     * Retrieves the name of an author given their ID.
     * Useful when displaying author name using only the stored author ID.
     *
     * @param authorId the unique ID of the author
     * @return the full name of the author
     */

    @GetMapping("/api/authors/{authorId}/name")
    String getAuthorNameById(@PathVariable("authorId") Long authorId);
}
