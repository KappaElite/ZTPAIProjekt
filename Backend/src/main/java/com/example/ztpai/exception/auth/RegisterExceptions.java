package com.example.ztpai.exception.auth;

public class RegisterExceptions {
    public static class UsernameTakenException extends RuntimeException {
        public UsernameTakenException(String message) {
            super(message);
        }
    }
    public static class EmailTakenException extends RuntimeException {
        public EmailTakenException(String message) {
            super(message);
        }
    }
}
