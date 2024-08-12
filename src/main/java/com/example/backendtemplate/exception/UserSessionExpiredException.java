package com.example.backendtemplate.exception;

public class UserSessionExpiredException extends RuntimeException {
    public UserSessionExpiredException(String message) {
        super(message);
    }
}
