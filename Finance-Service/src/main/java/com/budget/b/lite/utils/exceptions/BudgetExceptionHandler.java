package com.budget.b.lite.utils.exceptions;

import com.budget.b.lite.utils.exceptions.custom_exceptions.CategoryExistsException;
import com.budget.b.lite.utils.exceptions.custom_exceptions.CategoryNotFoundException;
import com.budget.b.lite.utils.exceptions.custom_exceptions.NoExpenseFoundException;
import com.budget.b.lite.utils.exceptions.custom_exceptions.NoIncomeFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class BudgetExceptionHandler {

    @ExceptionHandler({NoExpenseFoundException.class, NoIncomeFoundException.class,
            CategoryNotFoundException.class, CategoryExistsException.class})
    public ResponseEntity<Object> handleBudgetExceptions(RuntimeException ex){
        return buildResponse(HttpStatus.FORBIDDEN, ex.getMessage());

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleOtherExceptions(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong: " + ex.getMessage());
    }

    private ResponseEntity<Object> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return new ResponseEntity<>(body, status);
    }
}
