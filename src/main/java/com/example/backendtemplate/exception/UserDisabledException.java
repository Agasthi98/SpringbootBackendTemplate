package com.example.backendtemplate.exception;

import lombok.Getter;

@Getter
public class UserDisabledException extends RuntimeException {
    public UserDisabledException(String message) {
        super(message);
    }
}
