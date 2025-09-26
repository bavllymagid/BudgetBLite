package com.budget.b.lite.exception_handling;

import com.budget.b.lite.exception_handling.custom_exceptions.InvalidCredentialsException;
import com.budget.b.lite.exception_handling.custom_exceptions.InvalidTokenException;
import com.budget.b.lite.exception_handling.custom_exceptions.UserAlreadyExistsException;
import com.budget.b.lite.exception_handling.custom_exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class AuthExceptionHandler {
    // ---- User Login & Registration Exceptions ----
    @ExceptionHandler({UserAlreadyExistsException.class, InvalidCredentialsException.class})
    public ResponseEntity<Object> handleUserAuthExceptions(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // ---- Token Exceptions ----
    @ExceptionHandler({InvalidTokenException.class})
    public ResponseEntity<Object> handleTokenExceptions(RuntimeException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    // ---- User Management Exceptions ----
    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Object> handleUserManagementExceptions(RuntimeException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    // ---- Fallback for other exceptions ----
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherExceptions(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong: " + ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    // Utility method to build consistent error response
    private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }

}
