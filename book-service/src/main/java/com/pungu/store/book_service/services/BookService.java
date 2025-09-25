package com.pungu.store.book_service.services;

import com.pungu.store.book_service.dtos.BookRequest;
import com.pungu.store.book_service.dtos.BookResponse;
import com.pungu.store.book_service.dtos.SliceResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Service interface for managing books and their associated business operations.
 *
 * <p>Defines the contract for creating, updating, deleting, and retrieving books
 * within the bookstore application. Supports slice-based pagination for efficient
 * navigation of large datasets as well as specific queries (e.g., by ID or author).</p>
 */
@Service
public interface BookService {

    /**
     * Retrieves a slice of all books with pagination support.
     *
     * <p>This method does not return the total count of books but only indicates whether
     * another slice exists. This makes it efficient for large datasets and infinite
     * scrolling scenarios.</p>
     *
     * @param pageable pagination information (page index, size, and sort order)
     * @return a {@link SliceResponse} containing the current slice of {@link BookResponse} DTOs
     *         and pagination metadata
     */
    SliceResponse<BookResponse> getAllBooks(Pageable pageable);

    /**
     * Creates a new book entry in the system.
     *
     * @param bookRequest the request DTO containing book details
     * @return the created {@link BookResponse} DTO with persisted details
     */
    BookResponse createBook(BookRequest bookRequest);

    /**
     * Retrieves a book by its unique identifier.
     *
     * @param bookId the unique identifier of the book to retrieve
     * @return the {@link BookResponse} containing book details
     */
    BookResponse getBookById(Long bookId);

    /**
     * Retrieves all books written by a specific author.
     *
     * @param authorId the unique identifier of the author
     * @param pageable pagination and optional sorting information
     * @return a {@link SliceResponse} containing {@link BookResponse} DTOs for the given author
     */
    SliceResponse<BookResponse> getAllBookByAuthorId(Long authorId, Pageable pageable);

    /**
     * Updates the details of an existing book.
     *
     * @param bookId      the unique identifier of the book to update
     * @param bookRequest the DTO containing the updated book data
     * @return the updated {@link BookResponse}
     */
    BookResponse updateBook(Long bookId, BookRequest bookRequest);

    /**
     * Deletes a book from the system.
     *
     * @param bookId the unique identifier of the book to delete
     */
    void deleteBook(Long bookId);

    /**
     * Retrieves a slice of books whose titles start with the given prefix (case-insensitive).
     *
     * <p>Useful for implementing search-as-you-type or autocomplete functionality.</p>
     *
     * @param title    the title prefix to filter by (e.g., {@code "Ha"} for "Harry Potter")
     * @param pageable pagination information (page index, size, and sort order)
     * @return a {@link SliceResponse} containing the current slice of matching {@link BookResponse} DTOs
     */
    SliceResponse<BookResponse> getAllBooksStartingWithTitle(String title, Pageable pageable);
}