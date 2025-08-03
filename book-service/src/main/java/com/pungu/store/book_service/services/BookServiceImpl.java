package com.pungu.store.book_service.services;

import com.pungu.store.book_service.clients.AuthorClient;
import com.pungu.store.book_service.clients.RatingReviewClient;
import com.pungu.store.book_service.dtos.BookRequest;
import com.pungu.store.book_service.dtos.BookResponse;
import com.pungu.store.book_service.dtos.RatingResponse;
import com.pungu.store.book_service.entities.Book;
import com.pungu.store.book_service.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public BookResponse createBook(BookRequest bookRequest) {
        Long authorId = bookRequest.getAuthorName() != null ? authorClient.getAuthorIdByName(bookRequest.getAuthorName()) : bookRequest.getAuthorId();

        Book book = Book.builder()
                .title(bookRequest.getTitle())
                .authorId(authorId)
                .description(bookRequest.getDescription())
                .genre(bookRequest.getGenre())
                .language(bookRequest.getLanguage())
                .publicationDate(bookRequest.getPublicationDate())
                .ebookUrl(bookRequest.getEbookUrl())
                .coverImageUrl(bookRequest.getCoverImageUrl())
                .availableForReading(bookRequest.getEbookUrl() != null)
                .availableForDownload(bookRequest.getEbookUrl() != null)
                .build();

        return mapToResponse(bookRepository.save(book));
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

    @Override
    public List<BookResponse> getAllBookByAuthorId(Long authorId, Sort sort) {
        return bookRepository.findByAuthorId(authorId, sort).stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    /**
     * Returns all books in the database.
     *
     * @return List of BookResponse DTOs.
     */
    @Override
    public List<BookResponse> getAllBooks(Sort sort) {
        return bookRepository.findAll(sort)
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
    @Transactional
    public BookResponse updateBook(Long bookId, BookRequest bookRequest) {
        if (!bookRepository.existsById(bookId)) {
            throw new RuntimeException("Book not found");
        }

        Long authorId = bookRequest.getAuthorName() != null ? authorClient.getAuthorIdByName(bookRequest.getAuthorName()) : bookRequest.getAuthorId();

        Book updatedBook = Book.builder()
                .title(bookRequest.getTitle())
                .authorId(authorId)
                .description(bookRequest.getDescription())
                .genre(bookRequest.getGenre())
                .language(bookRequest.getLanguage())
                .publicationDate(bookRequest.getPublicationDate())
                .ebookUrl(bookRequest.getEbookUrl())
                .coverImageUrl(bookRequest.getCoverImageUrl())
                .availableForReading(bookRequest.getEbookUrl() != null)
                .availableForDownload(bookRequest.getEbookUrl() != null)
                .build();

        return mapToResponse(bookRepository.save(updatedBook));
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

        String authorName = book.getAuthorId() != null
                ? authorClient.getAuthorNameById(book.getAuthorId())
                : "Unknown Author";
        long authorId = book.getAuthorId() != null ? book.getAuthorId() : -1;

        return BookResponse.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .authorName(authorName)
                .authorId(authorId)
                .description(book.getDescription())
                .genre(book.getGenre())
                .coverImageUrl(book.getCoverImageUrl())
                .publicationDate(book.getPublicationDate())
                .averageRating(averageRating)
                .reviews(ratings)
                .availableForReading(book.isAvailableForReading())
                .availableForDownload(book.isAvailableForDownload())
                .language(book.getLanguage())
                .build();
    }

}