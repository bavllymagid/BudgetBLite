package com.budget.b.lite.exception_handling.custom_exceptions;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message){
        super(message);
    }
}
