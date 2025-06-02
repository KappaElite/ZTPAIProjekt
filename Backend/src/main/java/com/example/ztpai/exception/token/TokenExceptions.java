package com.example.ztpai.exception.token;

public class TokenExceptions {
    public static class RefreshTokenNotFound extends RuntimeException {
        public RefreshTokenNotFound(String message) {
            super(message);
        }
    }
}
