package com.example.ztpai.exception.auth;

public class LoginExceptions {
    public static class WrongPasswordException extends RuntimeException {
        public WrongPasswordException(String message) {
            super(message);
        }
    }
}
