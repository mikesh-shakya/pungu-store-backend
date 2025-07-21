package com.pungu.store.book_service.controllers;

import com.pungu.store.book_service.dtos.BookRequest;
import com.pungu.store.book_service.dtos.BookResponse;
import com.pungu.store.book_service.entities.Book;
import com.pungu.store.book_service.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/addBooks")
    public BookResponse createBook(@RequestBody BookRequest bookRequest) {
        return bookService.createBook(bookRequest);
    }
    @GetMapping("/{bookId}")
    public BookResponse getBookById(@PathVariable Long bookId) {
        return bookService.getBookById(bookId);
    }
    @GetMapping("/getAll")
    public List<BookResponse> getAllBooks() {
        return bookService.getAllBooks();
    }
    @PutMapping("/{bookId}")
    public BookResponse updateBook(@PathVariable Long bookId, @RequestBody BookRequest bookRequest) {
        return bookService.updateBook(bookId, bookRequest);
    }
    @DeleteMapping("/{bookId}")
    public void deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
    }
    // 📌 Upload Book with File
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadBook(
            @RequestPart("file") MultipartFile file,
            @RequestPart("book") Book book) {

        String fileName = bookService.uploadBookFile(file, book);
        return ResponseEntity.ok("Book uploaded successfully: " + fileName);
    }

    // 📌 Download Book File
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadBook(@PathVariable String fileName) {
        byte[] fileData = bookService.downloadBookFile(fileName);
        return ResponseEntity.ok(fileData);
    }

    // 📌 Delete Book File
    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteBook(@PathVariable String fileName) {
        bookService.deleteBookFile(fileName);
        return ResponseEntity.ok("Book file deleted: " + fileName);
    }

}
