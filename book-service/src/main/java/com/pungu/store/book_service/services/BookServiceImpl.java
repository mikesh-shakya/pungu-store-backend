package com.pungu.store.book_service.services;

import com.pungu.store.book_service.clients.AuthorClient;
import com.pungu.store.book_service.clients.FileStorageClient;
import com.pungu.store.book_service.clients.RatingReviewClient;
import com.pungu.store.book_service.dtos.BookRequest;
import com.pungu.store.book_service.dtos.BookResponse;
import com.pungu.store.book_service.dtos.RatingResponse;
import com.pungu.store.book_service.entities.Book;
import com.pungu.store.book_service.repositories.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorClient authorClient;
    @Autowired
    private FileStorageClient fileStorageClient;
    @Autowired
    private RatingReviewClient ratingReviewClient;

    @Override
    public BookResponse createBook(BookRequest bookRequest) {
        // Create book entity
        Book book = new Book();
        book.setTitle(bookRequest.getTitle());
        book.setAuthorId(authorClient.getAuthorIdByName(bookRequest.getAuthorName())); // Find author by name or create new one
        book.setDescription(bookRequest.getDescription());
        book.setCoverImageUrl(bookRequest.getCoverImageUrl());
        book.setPublicationDate(bookRequest.getPublicationYear());
        book.setAvailableForReading(bookRequest.getFileUrl() != null);
        book.setAvailableForDownload(bookRequest.getFileUrl() != null);
        // Save to DB
        book = bookRepository.save(book);
        return mapToResponse(book);
    }

    @Override
    public BookResponse getBookById(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        return mapToResponse(book);
    }

    @Override
    public List<BookResponse> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public BookResponse updateBook(Long bookId, BookRequest bookRequest) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));

        book.setTitle(bookRequest.getTitle());
        book.setDescription(bookRequest.getDescription());
        book.setCoverImageUrl(bookRequest.getCoverImageUrl());
        book.setPublicationDate(bookRequest.getPublicationYear());
        book.setAuthorId(authorClient.getAuthorIdByName(bookRequest.getAuthorName()));

        book = bookRepository.save(book);
        return mapToResponse(book);
    }

    @Override
    public void deleteBook(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow(() -> new RuntimeException("Book not found"));
        bookRepository.deleteById(bookId);
    }

    private BookResponse mapToResponse(Book book) {
        // Fetch Ratings & Reviews from Rating Service
        List<RatingResponse> ratings = ratingReviewClient.getRatingsForBook(book.getBookId());
        // Calculate Average Rating
        double averageRating =
                ratings.stream().mapToDouble(RatingResponse::getRating)
                .average()
                .orElse(0.0);

        return new BookResponse(
                book.getTitle(),
                authorClient.getAuthorName(book.getAuthorId()),
                book.getDescription(),
                book.getCoverImageUrl(),
                book.getPublicationDate(),
                averageRating,
                ratings,
                book.isAvailableForReading(),
                book.isAvailableForDownload()
        );
    }

    // 📌 Upload Book File & Save Book
    public String uploadBookFile(MultipartFile file, Book book) {
        String fileName = fileStorageClient.uploadFile(file).getBody();
        book.setCoverImageUrl(fileName);
        bookRepository.save(book);
        return fileName;
    }

    // 📌 Download Book File
    public byte[] downloadBookFile(String fileName) {
        return fileStorageClient.downloadFile(fileName).getBody();
    }

    // 📌 Delete Book File
    public void deleteBookFile(String fileName) {
        fileStorageClient.deleteFile(fileName);
    }
}
