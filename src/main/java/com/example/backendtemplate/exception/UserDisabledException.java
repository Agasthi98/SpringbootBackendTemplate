package com.example.backendtemplate.exception;

import lombok.Getter;

@Getter
public class UserDisabledException extends Exception {
    private final String message;
    public UserDisabledException(String message) {
        this.message = message;
    }
}
