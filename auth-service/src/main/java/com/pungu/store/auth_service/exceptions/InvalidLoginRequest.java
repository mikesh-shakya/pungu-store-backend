package com.pungu.store.auth_service.exceptions;

public class InvalidLoginRequest extends RuntimeException {
    public InvalidLoginRequest(String message) {
        super(message);
    }
}
