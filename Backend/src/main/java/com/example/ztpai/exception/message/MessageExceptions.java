package com.example.ztpai.exception.message;

public class MessageExceptions {
    public static class MessageCannotBeEmptyException extends RuntimeException {
        public MessageCannotBeEmptyException(String message) {
            super(message);
        }
    }
}
