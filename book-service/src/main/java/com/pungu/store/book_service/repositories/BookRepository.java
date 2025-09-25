package com.pungu.store.book_service.repositories;

import com.pungu.store.book_service.entities.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Book} entities.
 *
 * <p>Extends {@link JpaRepository} to provide standard CRUD functionality and adds
 * custom query methods for book-specific queries.</p>
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Retrieves a slice of all books based on the given {@link Pageable}.
     *
     * <p>Unlike a {@code Page}, a {@code Slice} does not include the total count of records,
     * which makes it more efficient for large datasets and infinite scrolling.</p>
     *
     * @param pageable pagination and sorting information
     * @return a {@link Slice} of {@link Book} entities
     */
    Slice<Book> findAllBy(Pageable pageable);

    /**
     * Retrieves all books written by the specified author, with pagination and optional sorting.
     *
     * @param authorId the ID of the author whose books should be fetched
     * @param pageable pagination and sorting information
     * @return a {@link Slice} of {@link Book} entities written by the specified author
     */
    Slice<Book> findByAuthorId(Long authorId, Pageable pageable);

    /**
     * Checks whether a book exists with the given ID.
     *
     * @param bookId the ID of the book to check
     * @return {@code true} if a book exists with the specified ID; {@code false} otherwise
     */
    boolean existsById(Long bookId);

    /**
     * Checks whether a book exists with the given title (case-insensitive).
     *
     * @param title the title of the book to check
     * @return {@code true} if a book exists with the given title; {@code false} otherwise
     */
    boolean existsByTitleIgnoreCase(String title);

    /**
     * Retrieves a slice of books whose title starts with the given prefix (case-insensitive).
     *
     * @param prefix   the title prefix to match (e.g. {@code "Ha"} matches {@code "Harry Potter"})
     * @param pageable pagination and sorting information
     * @return a {@link Slice} of {@link Book} entities matching the prefix
     */
    Slice<Book> findByTitleStartingWithIgnoreCase(String prefix, Pageable pageable);
}
