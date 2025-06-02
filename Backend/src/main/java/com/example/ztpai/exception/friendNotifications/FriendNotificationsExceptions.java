package com.example.ztpai.exception.friendNotifications;

public class FriendNotificationsExceptions {
    public static class RequestNotFound extends RuntimeException {
        public RequestNotFound(String message) {
            super(message);
        }
    }
    public static class RequestAlreadyExists extends RuntimeException {
        public RequestAlreadyExists(String message) {
            super(message);
        }
    }
    public static class RequestAlreadyAccepted extends RuntimeException {
        public RequestAlreadyAccepted(String message) {
            super(message);
        }
    }

    public static class RequestAlreadyRejected extends RuntimeException {
        public RequestAlreadyRejected(String message) {
            super(message);
        }
    }
}
