package com.pungu.store.rating_service.controllers;

import com.pungu.store.rating_service.dtos.RatingRequest;
import com.pungu.store.rating_service.dtos.RatingResponse;
import com.pungu.store.rating_service.dtos.SliceResponse;
import com.pungu.store.rating_service.services.RatingService;
import com.pungu.store.rating_service.utilities.SortUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for managing book ratings and reviews.
 *
 * <p>Exposes endpoints for adding or updating ratings, retrieving ratings by book or user,
 * and calculating the average rating of a book. Pagination and sorting are supported via
 * request parameters.</p>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ratings")
public class RatingController {

    private final RatingService ratingService;

    /**
     * Adds a new rating or updates an existing one for a given book and user.
     *
     * <p>If the user has already rated the book, the existing rating will be updated
     * with the new value and/or comment. Otherwise, a new rating entry will be created.</p>
     *
     * @param rating the {@link RatingRequest} containing {@code bookId}, {@code userId},
     *               {@code rating} value, and optional {@code review} comment
     * @return a {@link ResponseEntity} wrapping the saved or updated {@link RatingResponse}
     */
    @PostMapping
    public ResponseEntity<RatingResponse> addOrUpdateRating(@RequestBody RatingRequest rating) {
        return ResponseEntity.ok(ratingService.addOrUpdateRating(rating));
    }

    /**
     * Retrieves a slice of ratings for a specific book.
     *
     * <p>Supports pagination and optional sorting via query parameters. The results
     * are returned as a {@link SliceResponse}, which contains the content and
     * pagination metadata but does not include a total count (unlike {@code Page}).</p>
     *
     * @param bookId  the ID of the book
     * @param offset  the zero-based page index (default: {@code 0})
     * @param limit   the maximum number of records per page (default: {@code 20})
     * @param orderBy optional sort criteria, e.g. {@code "rating,desc"} or {@code "createdAt,asc"}
     * @return a {@link SliceResponse} containing ratings for the given book
     * @see SortUtils#parseSort(String)
     */
    @GetMapping("/book/{bookId}")
    public SliceResponse<RatingResponse> getRatingsByBook(
            @PathVariable("bookId") Long bookId,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "orderBy", required = false) String orderBy
    ) {
        Sort sort = SortUtils.parseSort(orderBy);
        Pageable pageable = PageRequest.of(offset, limit, sort);
        return ratingService.getRatingsByBook(bookId, pageable);
    }

    /**
     * Retrieves a slice of ratings submitted by a specific user.
     *
     * <p>Supports pagination and optional sorting via query parameters. The results
     * are returned as a {@link SliceResponse}, which contains the content and
     * pagination metadata but does not include a total count.</p>
     *
     * @param userId  the ID of the user
     * @param offset  the zero-based page index (default: {@code 0})
     * @param limit   the maximum number of records per page (default: {@code 20})
     * @param orderBy optional sort criteria, e.g. {@code "rating,desc"} or {@code "createdAt,asc"}
     * @return a {@link SliceResponse} containing ratings submitted by the user
     * @see SortUtils#parseSort(String)
     */
    @GetMapping("/user/{userId}")
    public SliceResponse<RatingResponse> getRatingsByUser(
            @PathVariable("userId") Long userId,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "orderBy", required = false) String orderBy
    ) {
        Sort sort = SortUtils.parseSort(orderBy);
        Pageable pageable = PageRequest.of(offset, limit, sort);
        return ratingService.getRatingsByUser(userId, pageable);
    }

    /**
     * Calculates the average rating for a specific book.
     *
     * <p>The result is returned in a simple JSON map of the form:
     * <pre>{@code
     * {
     *   "averageRating": 4.3
     * }
     * }</pre>
     *
     * @param bookId the ID of the book
     * @return a {@link ResponseEntity} containing a map with a single key
     *         {@code "averageRating"} mapped to the computed value
     */
    @GetMapping("/book/{bookId}/average")
    public ResponseEntity<Map<String, Object>> getAverageRating(@PathVariable("bookId") Long bookId) {
        Map<String, Object> response = new HashMap<>();
        response.put("averageRating", ratingService.getAverageRating(bookId));
        return ResponseEntity.ok(response);
    }
}
