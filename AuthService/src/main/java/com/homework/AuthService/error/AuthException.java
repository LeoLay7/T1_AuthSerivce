package com.homework.AuthService.error;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
