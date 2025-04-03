package com.pungu.store.book_service.services;

import com.pungu.store.book_service.dtos.BookRequest;
import com.pungu.store.book_service.dtos.BookResponse;
import com.pungu.store.book_service.entities.Book;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface BookService {
    BookResponse createBook(BookRequest bookRequest);
    BookResponse getBookById(Long bookId);
    List<BookResponse> getAllBooks();
    BookResponse updateBook(Long bookId, BookRequest bookRequest);
    void deleteBook(Long bookId);
    String uploadBookFile(MultipartFile file, Book book);
    byte[] downloadBookFile(String fileName);
    void deleteBookFile(String fileName);
}
