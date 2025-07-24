package com.pungu.store.author_service.exception;

public class DuplicateAuthorException extends RuntimeException {
    public DuplicateAuthorException(String message) {
        super(message);
    }
}
