package com.example.ztpai.exception;

import com.example.ztpai.exception.auth.LoginExceptions;
import com.example.ztpai.exception.auth.RegisterExceptions;
import com.example.ztpai.exception.friend.FriendExceptions;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(GlobalExceptions.UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(GlobalExceptions.UserNotFoundException ex) {
        return buildResponse(ex.getMessage(), 404);
    }


    @ExceptionHandler(LoginExceptions.WrongPasswordException.class)
    public ResponseEntity<?> handleWrongPassword(LoginExceptions.WrongPasswordException ex) {
        return buildResponse(ex.getMessage(), 401);
    }

    @ExceptionHandler(RegisterExceptions.EmailTakenException.class)
    public ResponseEntity<?> handleEmailTaken(RegisterExceptions.EmailTakenException ex) {
        return buildResponse(ex.getMessage(), 409);
    }

    @ExceptionHandler(RegisterExceptions.UsernameTakenException.class)
    public ResponseEntity<?> handleUsernameTaken(RegisterExceptions.UsernameTakenException ex) {
        return buildResponse(ex.getMessage(), 409);
    }


    @ExceptionHandler(FriendExceptions.FriendNotFoundException.class)
    public ResponseEntity<?> handleFriendNotFound(FriendExceptions.FriendNotFoundException ex) {
        return buildResponse(ex.getMessage(), 404);
    }

    @ExceptionHandler(FriendExceptions.AddingYourselfException.class)
    public ResponseEntity<?> handleAddingYourself(FriendExceptions.AddingYourselfException ex) {
        return buildResponse(ex.getMessage(), 400);
    }

    @ExceptionHandler(FriendExceptions.FriendshipAlreadyExistsException.class)
    public ResponseEntity<?> handleFriendshipAlreadyExists(FriendExceptions.FriendshipAlreadyExistsException ex) {
        return buildResponse(ex.getMessage(), 409);
    }

    @ExceptionHandler(FriendExceptions.DeletingYourselfException.class)
    public ResponseEntity<?> handleDeletingYourself(FriendExceptions.DeletingYourselfException ex) {
        return buildResponse(ex.getMessage(), 400);
    }

    @ExceptionHandler(FriendExceptions.FriendshipAlreadyDeletedException.class)
    public ResponseEntity<?> handleFriendshipAlreadyDeleted(FriendExceptions.FriendshipAlreadyDeletedException ex) {
        return buildResponse(ex.getMessage(), 410);
    }


    private ResponseEntity<Map<String, Object>> buildResponse(String message, int status) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", message);
        body.put("status", status);
        return ResponseEntity.status(status).body(body);
    }
}