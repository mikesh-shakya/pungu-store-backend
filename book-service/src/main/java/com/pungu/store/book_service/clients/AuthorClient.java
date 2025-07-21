package com.pungu.store.book_service.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "author-service", url = "http://localhost:8082/api/authors") // Update the actual URL
public interface AuthorClient {
    @GetMapping("/get-id")
    Long getAuthorIdByName(@RequestParam String authorName); // Fetch author ID by name

    @GetMapping("/{authorId}/name")
    String getAuthorName(@RequestParam Long authorId); // Fetch author name by ID
}
