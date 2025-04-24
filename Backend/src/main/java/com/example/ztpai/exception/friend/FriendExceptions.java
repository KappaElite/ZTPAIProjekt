package com.example.ztpai.exception.friend;

public class FriendExceptions {

    public static class FriendNotFoundException extends RuntimeException {
        public FriendNotFoundException(String message) {
            super(message);
        }
    }

    public static class AddingYourselfException extends RuntimeException {
        public AddingYourselfException(String message) {
            super(message);
        }
    }

    public static class FriendshipAlreadyExistsException extends RuntimeException {
        public FriendshipAlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class DeletingYourselfException extends RuntimeException {
        public DeletingYourselfException(String message) {
            super(message);
        }
    }

    public static class FriendshipAlreadyDeletedException extends RuntimeException {
        public FriendshipAlreadyDeletedException(String message) {
            super(message);
        }
    }

}
