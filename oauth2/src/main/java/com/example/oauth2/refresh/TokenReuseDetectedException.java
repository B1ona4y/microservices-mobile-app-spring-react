package com.example.oauth2.refresh;

public class TokenReuseDetectedException extends RuntimeException {
    public TokenReuseDetectedException(String message) {
        super(message);
    }
}
