package com.pungu.store.book_service.controllers;

import com.pungu.store.book_service.dtos.BookRequest;
import com.pungu.store.book_service.dtos.BookResponse;
import com.pungu.store.book_service.dtos.SliceResponse;
import com.pungu.store.book_service.services.BookService;
import com.pungu.store.book_service.utilities.SortUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    /**
     * Retrieves a slice (page-like segment) of books.
     *
     * <p>Pagination uses zero-based {@code offset} and {@code limit}. Sorting can be supplied via
     * {@code orderBy}. Examples:</p>
     * <ul>
     *   <li>{@code orderBy=title:asc,publicationDate:desc}</li>
     *   <li>{@code orderBy=-title,publicationDate}  (dash prefix means DESC)</li>
     * </ul>
     *
     * @param offset  the page index (0-based)
     * @param limit   the maximum number of books to return per page
     * @param orderBy optional sort criteria; see examples above
     * @param title   optional filter — return books whose title starts with this value (case-insensitive)
     * @return a {@link SliceResponse} containing the current slice of {@link BookResponse} objects
     */
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public SliceResponse<BookResponse> getAllBooks(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "orderBy", required = false) String orderBy,
            @RequestParam(value = "title", required = false) String title
    ) {
        Sort sort = SortUtils.parseSort(orderBy);
        Pageable pageable = PageRequest.of(offset, limit, sort);

        if (title == null || title.isEmpty()) {
            return bookService.getAllBooks(pageable);
        } else {
            return bookService.getAllBooksStartingWithTitle(title, pageable);
        }
    }

    /**
     * Creates a new book.
     *
     * @param bookRequest the DTO containing new book details (validated)
     * @return the created {@link BookResponse} with HTTP 201 (Created)
     */
    @PostMapping()
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest bookRequest) {
        BookResponse response = bookService.createBook(bookRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieves a slice (page-like segment) of all books for a given author.
     *
     * <p>Pagination uses zero-based {@code offset} and {@code limit}. Sorting can be supplied via
     * {@code orderBy}. Examples:</p>
     * <ul>
     *   <li>{@code orderBy=title:asc,publicationDate:desc}</li>
     *   <li>{@code orderBy=-title,publicationDate}  (dash prefix means DESC)</li>
     * </ul>
     *
     * @param offset  the page index (0-based)
     * @param limit   the maximum number of books to return per page
     * @param orderBy optional sort criteria; see examples above
     * @param authorId ID of the author
     * @return a {@link SliceResponse} containing the current slice of {@link BookResponse} objects.
     */
    @GetMapping("/author/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    public SliceResponse<BookResponse> getAllBooksByAuthor(@PathVariable("authorId") Long authorId,
                                                  @RequestParam(value = "offset", defaultValue = "0") int offset,
                                                  @RequestParam(value = "limit", defaultValue = "20") int limit,
                                                  @RequestParam(value = "orderBy", required = false) String orderBy) {
        Sort sort = SortUtils.parseSort(orderBy);
        Pageable pageable = PageRequest.of(offset, limit, sort);
        return bookService.getAllBookByAuthorId(authorId, pageable);
    }

    /**
     * Fetches a single book by id.
     *
     * @param bookId ID of the book
     * @return {@link BookResponse} and HTTP 200 (OK)
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable("bookId") Long bookId) {
        return new ResponseEntity<>(bookService.getBookById(bookId), HttpStatus.OK);
    }

    /**
     * Updates an existing book.
     *
     * @param bookId      ID of the book to update
     * @param bookRequest validated DTO containing updated values
     * @return the updated {@link BookResponse}
     */
    @PutMapping("/{bookId}")
    public BookResponse updateBook(@PathVariable("bookId") Long bookId, @Valid @RequestBody BookRequest bookRequest) {
        return bookService.updateBook(bookId, bookRequest);
    }

    /**
     * Deletes a book by id.
     *
     * @param bookId ID of the book to delete
     */
    @DeleteMapping("/{bookId}")
    public void deleteBook(@PathVariable("bookId") Long bookId) {
        bookService.deleteBook(bookId);
    }
}
