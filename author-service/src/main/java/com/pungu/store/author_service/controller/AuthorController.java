package com.pungu.store.author_service.controller;

import com.pungu.store.author_service.dto.AuthorRequest;
import com.pungu.store.author_service.dto.AuthorResponse;
import com.pungu.store.author_service.service.AuthorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * REST controller for managing authors.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    /**
     * Creates a new author.
     *
     * @param request the author details
     * @return the created author response
     */
    @PostMapping()
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody AuthorRequest request) {
        return ResponseEntity.ok(authorService.createAuthor(request));
    }

    /**
     * Retrieves an author by ID.
     *
     * @param authorId the ID of the author
     * @return the author response
     */
    @GetMapping("/{authorId}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable("authorId") Long authorId) {
        return ResponseEntity.ok(authorService.getAuthorById(authorId));
    }

    /**
     * Retrieves all authors.
     *
     * @return a list of all author responses
     */
    @GetMapping()
    public ResponseEntity<List<AuthorResponse>> getAllAuthors(@RequestParam(value = "sortBy", required = false) String sortBy) {
        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isBlank()) {
            List<Sort.Order> orders = Arrays.stream(sortBy.split(","))
                    .map(String::trim)
                    .map(field -> new Sort.Order(Sort.Direction.ASC, field))
                    .toList();

            sort = Sort.by(orders);
        }
        return ResponseEntity.ok(authorService.getAllAuthors(sort));
    }

    /**
     * Updates an existing author by ID.
     *
     * @param authorId the ID of the author to update
     * @param request the updated author details
     * @return the updated author response
     */
    @PutMapping("/{authorId}")
    public ResponseEntity<AuthorResponse> updateAuthor(
            @PathVariable("authorId") Long authorId,
            @Valid @RequestBody AuthorRequest request
    ) {
        return ResponseEntity.ok(authorService.updateAuthor(authorId, request));
    }

    /**
     * Deletes an author by ID.
     *
     * @param authorId the ID of the author to delete
     * @return a success message
     */
    @DeleteMapping("/{authorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<String> deleteAuthor(@PathVariable("authorId") Long authorId) {
        authorService.deleteAuthor(authorId);
        return ResponseEntity.ok("Author deleted successfully");
    }

    /**
     * Retrieves the ID of an author based on their name.
     *
     * @param authorName the name of the author
     * @return the ID of the author
     */
    @GetMapping("/getId")
    public ResponseEntity<Long> getAuthorIdByName(@RequestParam("authorName") String authorName) {
        return ResponseEntity.ok(authorService.getAuthorIdByName(authorName));
    }

    /**
     * Retrieves the name of an author based on their ID.
     *
     * @param authorId the ID of the author
     * @return the name of the author
     */
    @GetMapping("/{authorId}/name")
    public ResponseEntity<String> getAuthorNameById(@PathVariable("authorId") Long authorId) {
        return ResponseEntity.ok(authorService.getAuthorNameById(authorId));
    }

}