package com.pungu.store.api_gateway_service;

public class AuthServiceUnavailableException extends RuntimeException {
        public AuthServiceUnavailableException(String message) {
            super(message);
        }
    }