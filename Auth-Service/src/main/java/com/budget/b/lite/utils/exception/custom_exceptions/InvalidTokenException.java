package com.budget.b.lite.utils.exception.custom_exceptions;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message){
        super(message);
    }
}
