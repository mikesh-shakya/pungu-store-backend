package com.pungu.store.book_service.controllers;

import com.pungu.store.book_service.dtos.BookRequest;
import com.pungu.store.book_service.dtos.BookResponse;
import com.pungu.store.book_service.services.BookService;
import com.pungu.store.book_service.utilities.SortUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    /**
     * Retrieves all books.
     *
     * @return List of all BookResponse objects
     */
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<BookResponse> getAllBooks(@RequestParam(value = "sortBy", required = false) String sortBy) {
        Sort sort = SortUtils.parseSort(sortBy);
        return bookService.getAllBooks(sort);
    }

    /**
     * Adds a new book using provided book details.
     *
     * @param bookRequest BookRequest DTO containing book details
     * @return Created BookResponse
     */
    @PostMapping()
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest bookRequest) {
        BookResponse response = bookService.createBook(bookRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Fetches all books by author ID.
     *
     * @param authorId ID of the author
     * @return List of all BookResponse for the specified ID
     */
    @GetMapping("/author/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    public List<BookResponse> getAllBooksByAuthor(@PathVariable("authorId") Long authorId, @RequestParam(value = "sortBy", required = false) String sortBy) {
        Sort sort = SortUtils.parseSort(sortBy);
        return bookService.getAllBookByAuthorId(authorId, sort);
    }

    /**
     * Fetches a book by its ID.
     *
     * @param bookId ID of the book
     * @return BookResponse for the specified ID
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable("bookId") Long bookId) {
        return new ResponseEntity<>(bookService.getBookById(bookId), HttpStatus.OK);
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
    public void deleteBook(@PathVariable("bookId") Long bookId) {
        bookService.deleteBook(bookId);
    }
}
