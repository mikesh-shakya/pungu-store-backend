package com.pungu.store.book_service.controllers;

import com.pungu.store.book_service.dtos.BookRequest;
import com.pungu.store.book_service.dtos.BookResponse;
import com.pungu.store.book_service.services.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    /**
     * Adds a new book using provided book details.
     *
     * @param bookRequest BookRequest DTO containing book details
     * @return Created BookResponse
     */
    @PostMapping()
    public BookResponse createBook(@Valid @RequestBody BookRequest bookRequest) {
        return bookService.createBook(bookRequest);
    }

    /**
     * Fetches a book by its ID.
     *
     * @param bookId ID of the book
     * @return BookResponse for the specified ID
     */
    @GetMapping("/{bookId}")
    public BookResponse getBookById(@PathVariable("bookId") Long bookId) {
        return bookService.getBookById(bookId);
    }

    /**
     * Fetches all books by author ID.
     *
     * @param authorId ID of the author
     * @return List of all BookResponse for the specified ID
     */
    @GetMapping("/author/{authorId}")
    public List<BookResponse> getAllBooksByAuthor(@PathVariable("authorId") Long authorId, @RequestParam(value = "sortBy", required = false) String sortBy) {
        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isBlank()) {
            List<Sort.Order> orders = Arrays.stream(sortBy.split(","))
                    .map(String::trim)
                    .map(field -> new Sort.Order(Sort.Direction.ASC, field))
                    .toList();

            sort = Sort.by(orders);
        }
        return bookService.getAllBookByAuthorId(authorId, sort);
    }

    /**
     * Retrieves all books.
     *
     * @return List of all BookResponse objects
     */
    @GetMapping("")
    public List<BookResponse> getAllBooks(@RequestParam(value = "sortBy", required = false) String sortBy) {
        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isBlank()) {
            List<Sort.Order> orders = Arrays.stream(sortBy.split(","))
                    .map(String::trim)
                    .map(field -> new Sort.Order(Sort.Direction.ASC, field))
                    .toList();

            sort = Sort.by(orders);
        }
        return bookService.getAllBooks(sort);
    }

    /**
     * Updates an existing book with the given ID.
     *
     * @param bookId      ID of the book to update
     * @param bookRequest Updated book data
     * @return Updated BookResponse
     */
    @PutMapping("/{bookId}")
    public BookResponse updateBook(@PathVariable("bookId") Long bookId, @Valid @RequestBody BookRequest bookRequest) {
        return bookService.updateBook(bookId, bookRequest);
    }

    /**
     * Deletes a book by ID.
     *
     * @param bookId ID of the book to delete
     */
    @DeleteMapping("/{bookId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable("bookId") Long bookId) {
        bookService.deleteBook(bookId);
    }
}
