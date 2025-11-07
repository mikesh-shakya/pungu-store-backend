package com.pungu.store.api_gateway_service.exceptions;

import org.springframework.http.HttpStatus;

public class BookNotFoundException extends RuntimeException {
    private final HttpStatus status;

    public BookNotFoundException(Long bookId) {
        super("Book not found with id " + bookId);
        this.status = HttpStatus.NOT_FOUND;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
