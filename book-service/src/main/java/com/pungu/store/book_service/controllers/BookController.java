package com.pungu.store.book_service.controllers;

import com.pungu.store.book_service.dtos.BookRequest;
import com.pungu.store.book_service.dtos.BookResponse;
import com.pungu.store.book_service.services.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public BookResponse createBook(@RequestBody BookRequest bookRequest) {
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
     * Retrieves all books.
     *
     * @return List of all BookResponse objects
     */
    @GetMapping("")
    public List<BookResponse> getAllBooks() {
        return bookService.getAllBooks();
    }

    /**
     * Updates an existing book with the given ID.
     *
     * @param bookId ID of the book to update
     * @param bookRequest Updated book data
     * @return Updated BookResponse
     */
    @PutMapping("/{bookId}")
    public BookResponse updateBook(@PathVariable("bookId") Long bookId, @RequestBody BookRequest bookRequest) {
        return bookService.updateBook(bookId, bookRequest);
    }

    /**
     * Deletes a book by ID.
     *
     * @param bookId ID of the book to delete
     */
    @DeleteMapping("/{bookId}")
    public void deleteBook(@PathVariable("bookId") Long bookId) {
        bookService.deleteBook(bookId);
    }
}
