package com.pungu.store.book_service.services;

import com.pungu.store.book_service.dtos.BookRequest;
import com.pungu.store.book_service.dtos.BookResponse;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service interface for managing books and their associated files.
 * Defines business operations related to books in the bookstore application.
 */
@Service
public interface BookService {

    /**
     * Creates a new book entry.
     *
     * @param bookRequest The book request data containing book details.
     * @return The created book as a BookResponse.
     */
    BookResponse createBook(BookRequest bookRequest);

    /**
     * Retrieves a book by its ID.
     *
     * @param bookId The ID of the book.
     * @return BookResponse containing book details.
     */
    BookResponse getBookById(Long bookId);

    /**
     * Retrieves all book by an author ID.
     *
     * @param authorId The ID of the author.
     * @return BookResponse containing book details.
     */
    List<BookResponse> getAllBookByAuthorId(Long authorId, Sort sort);

    /**
     * Retrieves all books.
     *
     * @return A list of BookResponse objects.
     */
    List<BookResponse> getAllBooks(Sort sort);

    /**
     * Updates an existing book's details.
     *
     * @param bookId      The ID of the book to be updated.
     * @param bookRequest The updated book data.
     * @return The updated BookResponse.
     */
    BookResponse updateBook(Long bookId, BookRequest bookRequest);

    /**
     * Deletes a book by its ID.
     *
     * @param bookId The ID of the book to be deleted.
     */
    void deleteBook(Long bookId);
}
