package com.pungu.store.book_service.services;

import com.pungu.store.book_service.clients.AuthorClient;
import com.pungu.store.book_service.dtos.BookRequest;
import com.pungu.store.book_service.dtos.BookResponse;
import com.pungu.store.book_service.dtos.SliceResponse;
import com.pungu.store.book_service.entities.Book;
import com.pungu.store.book_service.exceptions.BookAlreadyExistsException;
import com.pungu.store.book_service.exceptions.BookNotFoundException;
import com.pungu.store.book_service.repositories.BookRepository;
import com.pungu.store.book_service.utilities.SliceResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service implementation for managing {@link Book} entities and producing {@link BookResponse} DTOs.
 *
 * <p>This class handles common book operations such as listing (slice-based pagination),
 * creation, update, retrieval and deletion. It also consults the {@link AuthorClient} to
 * resolve an author's display name when creating or updating books.</p>
 *
 * <p>Transactional and security annotations are applied to methods that modify state.</p>
 *
 * @since 1.0
 */
@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorClient authorClient;

    /**
     * Returns a slice (page-like segment) of books according to the provided {@link Pageable}.
     *
     * <p>The returned {@link SliceResponse} contains the mapped {@link BookResponse} content as
     * well as pagination metadata (hasNext, page size, etc.) produced by {@link SliceResponseUtil}.
     * The method uses {@link BookRepository#findAllBy(Pageable)} to perform an efficient slice
     * query (no total count).</p>
     *
     * @param pageable the pagination information (page index, page size, sort)
     * @return a {@link SliceResponse} containing the page of {@link BookResponse} objects and pagination metadata
     */
    @Override
    public SliceResponse<BookResponse> getAllBooks(Pageable pageable) {
        // fetch a slice of Book entities
        Slice<Book> bookList = bookRepository.findAllBy(pageable);
        List<BookResponse> content = bookList.stream()
                .map(this::entityToResponse)
                .toList();
        return SliceResponseUtil.mapToSLiceResponse(content, bookList);
    }


    /**
     * Creates a new {@link Book} from the supplied {@link BookRequest} and persists it.
     *
     * <p>If a book with the same title (case-insensitive) already exists, a {@link BookAlreadyExistsException}
     * is thrown.>
     * It is transactional and restricted to users with the ADMIN role.</p>
     *
     * @param bookRequest the request DTO containing the book data to create; must contain at least a title
     * @return the created {@link BookResponse} representing the persisted entity
     * @throws BookAlreadyExistsException if a book with the same title already exists (case-insensitive)
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
                .description(bookRequest.getDescription())
                .genre(bookRequest.getGenre())
                .language(bookRequest.getLanguage())
                .publicationDate(bookRequest.getPublicationDate())
                .coverImageUrl(bookRequest.getCoverImageUrl())
                .build();

        return entityToResponse(bookRepository.save(book));
    }


    /**
     * Returns all books by the specified author, using slice-based pagination.
     *
     * <p>This method returns a {@link SliceResponse} built from a {@link Slice} query,
     * which avoids an expensive total-count query and is suitable for larger result sets.
     * For small result sets where a full list is required, callers can adapt accordingly.</p>
     *
     * @param authorId the id of the author whose books should be returned; must not be {@code null}
     * @param pageable pagination and optional sorting information
     * @return a {@link SliceResponse} of {@link BookResponse} objects for the given author
     */
    @Override
    public SliceResponse<BookResponse> getAllBookByAuthorId(Long authorId, Pageable pageable) {
        Slice<Book> bookList = bookRepository.findByAuthorId(authorId, pageable);
        List<BookResponse> content = bookList.stream()
                .map(this::entityToResponse)
                .toList();
        return SliceResponseUtil.mapToSLiceResponse(content, bookList);
    }

    /**
     * Retrieves a single book by its id.
     *
     * @param bookId the id of the book to retrieve; must not be {@code null}
     * @return the {@link BookResponse} mapped from the found entity
     * @throws BookNotFoundException if no book exists with the given id
     */
    @Override
    public BookResponse getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("There is no book found for this book id " + bookId));
        return entityToResponse(book);
    }


    /**
     * Updates an existing book identified by {@code bookId} with the values from {@code bookRequest}.
     *
     * <p>The method first validates that the book exists; if not, a {@link BookNotFoundException} is thrown.
     * It then updates fields and persists the changes. This operation is transactional and requires ADMIN role.</p>
     *
     * @param bookId the id of the book to update; must not be {@code null}
     * @param bookRequest the DTO containing updated book values
     * @return the updated {@link BookResponse} representing the saved entity
     * @throws BookNotFoundException if the book with the supplied id does not exist
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
        book.setDescription(bookRequest.getDescription());
        book.setGenre(bookRequest.getGenre());
        book.setLanguage(bookRequest.getLanguage());
        book.setPublicationDate(bookRequest.getPublicationDate());
        book.setCoverImageUrl(bookRequest.getCoverImageUrl());

        Book saved = bookRepository.save(book);
        return entityToResponse(saved);
    }


    /**
     * Deletes the book identified by {@code bookId}.
     *
     * <p>If the book does not exist a {@link BookNotFoundException} will be thrown. This operation is
     * transactional and requires ADMIN privileges.</p>
     *
     * @param bookId the id of the book to delete; must not be {@code null}
     * @throws BookNotFoundException if no book exists with the given id
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
     * Returns a slice of books whose title starts with the given prefix (case-insensitive).
     *
     * @param title the title prefix to filter by; if blank or {@code null} this method will return
     *              an empty slice depending on repository behavior
     * @param pageable pagination information (page index, size and sort)
     * @return a {@link SliceResponse} containing matched {@link BookResponse} objects and pagination metadata
     */
    @Override
    public SliceResponse<BookResponse> getAllBooksStartingWithTitle(String title, Pageable pageable) {
        Slice<Book> bookSlice = bookRepository.findByTitleStartingWithIgnoreCase(title, pageable);
        List<BookResponse> content = bookSlice.stream()
                .map(this::entityToResponse)
                .toList();
        return SliceResponseUtil.mapToSLiceResponse(content, bookSlice);
    }

    /**
     * Maps a {@link Book} entity to its {@link BookResponse} DTO.
     *
     * <p>Mapping currently copies primary fields. Ratings or other external data can be
     * merged here if required by fetching from other services.</p>
     *
     * @param book the entity to map; must not be {@code null}
     * @return the populated {@link BookResponse} DTO
     */
    private BookResponse entityToResponse(Book book) {
        return BookResponse.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .authorId(book.getAuthorId())
                .description(book.getDescription())
                .genre(book.getGenre())
                .language(book.getLanguage())
                .publicationDate(book.getPublicationDate())
                .coverImageUrl(book.getCoverImageUrl())
                .build();
    }

    /**
     * Helper that resolves an author's display name using {@link AuthorClient}.
     *
     * <p>If {@code authorId} is {@code null} this method returns the constant {@code "Unknown Author"}.
     * Otherwise, it will call the author client and return whatever value is returned by the client
     * (callers should handle potential downstream errors).</p>
     *
     * @param authorId id of the author to resolve, may be {@code null}
     * @return the resolved author name or {@code "Unknown Author"} when {@code authorId} is {@code null}
     */
    private String getAuthorName(Long authorId) {
        String authorName = "Unknown Author";

        if (authorId != null) {
            authorName = authorClient.getAuthorNameById(authorId);
        }
        return authorName;
    }

}
