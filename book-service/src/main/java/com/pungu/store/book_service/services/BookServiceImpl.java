package com.pungu.store.book_service.services;

import com.pungu.store.book_service.clients.AuthorClient;
import com.pungu.store.book_service.clients.RatingReviewClient;
import com.pungu.store.book_service.dtos.BookRequest;
import com.pungu.store.book_service.dtos.BookResponse;
import com.pungu.store.book_service.dtos.RatingResponse;
import com.pungu.store.book_service.entities.Book;
import com.pungu.store.book_service.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final AuthorClient authorClient;

    private final RatingReviewClient ratingReviewClient;

    /**
     * Creates a new book entry in the system.
     *
     * @param bookRequest Request DTO containing book details.
     * @return BookResponse DTO with the created book's details.
     */
    @Override
    public BookResponse createBook(BookRequest bookRequest) {
        Book book = new Book();
        book.setTitle(bookRequest.getTitle());
        book.setAuthorId(bookRequest.getAuthorId());
        book.setDescription(bookRequest.getDescription());
        book.setCoverImageUrl(bookRequest.getCoverImageUrl());
        book.setPublicationDate(bookRequest.getPublicationYear());
        book.setAvailableForReading(bookRequest.getFileUrl() != null);
        book.setAvailableForDownload(bookRequest.getFileUrl() != null);

        book = bookRepository.save(book);
        return mapToResponse(book);
    }

    /**
     * Retrieves a book by its ID.
     *
     * @param bookId ID of the book.
     * @return BookResponse with book details.
     */
    @Override
    public BookResponse getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return mapToResponse(book);
    }

    /**
     * Returns all books in the database.
     *
     * @return List of BookResponse DTOs.
     */
    @Override
    public List<BookResponse> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing book.
     *
     * @param bookId      ID of the book to update.
     * @param bookRequest DTO with updated data.
     * @return Updated BookResponse.
     */
    @Override
    public BookResponse updateBook(Long bookId, BookRequest bookRequest) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        book.setTitle(bookRequest.getTitle());
        book.setDescription(bookRequest.getDescription());
        book.setCoverImageUrl(bookRequest.getCoverImageUrl());
        book.setPublicationDate(bookRequest.getPublicationYear());
        book.setAuthorId(bookRequest.getAuthorId());

        book = bookRepository.save(book);
        return mapToResponse(book);
    }

    /**
     * Deletes a book by its ID.
     *
     * @param bookId ID of the book to delete.
     */
    @Override
    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        bookRepository.deleteById(bookId);
    }

    /**
     * Maps a Book entity to a BookResponse DTO.
     * Includes ratings and average rating fetched from the rating service.
     *
     * @param book Book entity to map.
     * @return BookResponse with all book details.
     */
    private BookResponse mapToResponse(Book book) {
        List<RatingResponse> ratings = ratingReviewClient.getRatingsForBook(book.getBookId());
        double averageRating = ratings.stream()
                .mapToDouble(RatingResponse::getRating)
                .average()
                .orElse(0.0);

        String authorName = authorClient.getAuthorNameById(book.getAuthorId());

        return new BookResponse(
                book.getTitle(),
                authorName,
                book.getDescription(),
                book.getCoverImageUrl(),
                book.getPublicationDate(),
                averageRating,
                ratings,
                book.isAvailableForReading(),
                book.isAvailableForDownload()
        );
    }
}