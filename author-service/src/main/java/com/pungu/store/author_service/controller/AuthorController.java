package com.pungu.store.author_service.controller;

import com.pungu.store.author_service.dto.AuthorRequest;
import com.pungu.store.author_service.dto.AuthorResponse;
import com.pungu.store.author_service.dto.SliceResponse;
import com.pungu.store.author_service.service.AuthorService;
import com.pungu.store.author_service.utilities.SortUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing authors.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    /**
     * Retrieves all authors.
     *
     * @param offset the page index (0-based)
     * @param limit the maximum number of authors to return per page
     * @param orderBy optional sort criteria, e.g. "fullName:asc,dateOfBirth:desc" or "-name,createdDate" (dash prefix means DESC)
     * @param prefix optional filter to match authors whose full name or pen name starts with the given value
     *
     * @return a list of all author responses
     */
    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public SliceResponse<AuthorResponse> getAllAuthors(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "name", required = false) String prefix
    ) {
        Pageable pageable = PageRequest.of(offset, limit, SortUtils.parseSort(orderBy));

        if (prefix == null || prefix.isEmpty()) {
            return authorService.getAllAuthors(pageable);
        } else {
            return authorService.getAuthorByFullNameOrPenNameStartingWith(prefix, pageable);
        }
    }


    /**
     * Creates a new author.
     *
     * @param request the author details
     * @return the created author response
     */
    @PostMapping()
    public ResponseEntity<AuthorResponse> createAuthor(@Valid @RequestBody AuthorRequest request) {
        AuthorResponse response = authorService.createAuthor(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves an author by ID.
     *
     * @param authorId the ID of the author
     * @return the author response
     */
    @GetMapping("/{authorId}")
    public ResponseEntity<AuthorResponse> getAuthorById(@PathVariable("authorId") Long authorId) {
        return new ResponseEntity<>(authorService.getAuthorById(authorId), HttpStatus.OK);
    }


    /**
     * Updates an existing author by ID.
     *
     * @param authorId the ID of the author to update
     * @param request  the updated author details
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