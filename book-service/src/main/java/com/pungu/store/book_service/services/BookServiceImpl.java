package com.pungu.store.book_service.services;

import com.pungu.store.book_service.clients.AuthorClient;
import com.pungu.store.book_service.dtos.BookRequest;
import com.pungu.store.book_service.dtos.BookResponse;
import com.pungu.store.book_service.entities.Book;
import com.pungu.store.book_service.exceptions.BookAlreadyExistsException;
import com.pungu.store.book_service.exceptions.BookNotFoundException;
import com.pungu.store.book_service.repositories.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorClient authorClient;

    /**
     * Returns all books in the database.
     *
     * @return List of BookResponse DTOs.
     */
    @Override
    public List<BookResponse> getAllBooks(Sort sort) {
        return bookRepository.findAll(sort)
                .stream()
                .map(this::entityToResponse)
                .collect(Collectors.toList());
    }


    /**
     * Creates a new book entry in the system.
     *
     * @param bookRequest Request DTO containing book details.
     * @return BookResponse DTO with the created book's details.
     */
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public BookResponse createBook(BookRequest bookRequest) {
        if (bookRepository.existsByTitleIgnoreCase(bookRequest.getTitle())) {
            throw new BookAlreadyExistsException("Book with this title already exists.");
        }

        Book book = Book.builder()
                .title(bookRequest.getTitle())
                .authorId(bookRequest.getAuthorId())
                .authorName(getAuthorName(bookRequest.getAuthorId()))
                .description(bookRequest.getDescription())
                .genre(bookRequest.getGenre())
                .language(bookRequest.getLanguage())
                .publicationDate(bookRequest.getPublicationDate())
                .coverImageUrl(bookRequest.getCoverImageUrl())
                .build();

        return entityToResponse(bookRepository.save(book));
    }


    /**
     * Returns all books for a particular author in the database.
     *
     * @return List of BookResponse DTOs.
     */
    @Override
    public List<BookResponse> getAllBookByAuthorId(Long authorId, Sort sort) {
        return bookRepository.findByAuthorId(authorId, sort).stream().map(this::entityToResponse).collect(Collectors.toList());
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
                .orElseThrow(() -> new BookNotFoundException("There is no book found for this book id " + bookId));
        return entityToResponse(book);
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
    @PreAuthorize("hasRole('ADMIN')")
    public BookResponse updateBook(Long bookId, BookRequest bookRequest) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(
                        "There is no book found for this book id " + bookId));

        book.setTitle(bookRequest.getTitle());
        book.setAuthorId(bookRequest.getAuthorId());
        book.setAuthorName(getAuthorName(bookRequest.getAuthorId()));
        book.setDescription(bookRequest.getDescription());
        book.setGenre(bookRequest.getGenre());
        book.setLanguage(bookRequest.getLanguage());
        book.setPublicationDate(bookRequest.getPublicationDate());
        book.setCoverImageUrl(bookRequest.getCoverImageUrl());

        Book saved = bookRepository.save(book);
        return entityToResponse(saved);
    }


    /**
     * Deletes a book by its ID.
     *
     * @param bookId ID of the book to delete.
     */
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteBook(Long bookId) {
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException("There is no book found for this book id " + bookId);
        }
        bookRepository.deleteById(bookId);
    }

    /**
     * Maps a Book entity to a BookResponse DTO.
     * Includes ratings and average rating fetched from the rating service.
     *
     * @param book Book entity to map.
     * @return BookResponse with all book details.
     */
    private BookResponse entityToResponse(Book book) {
        return BookResponse.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .authorId(book.getAuthorId())
                .authorName(book.getAuthorName())
                .description(book.getDescription())
                .genre(book.getGenre())
                .language(book.getLanguage())
                .publicationDate(book.getPublicationDate())
                .coverImageUrl(book.getCoverImageUrl())
                .build();
    }


    private String getAuthorName(Long authorId) {
        String fetch_author_name = "Unknown Author";

        if (authorId != null) {
            fetch_author_name = authorClient.getAuthorNameById(authorId);
        }
        return fetch_author_name;
    }

}