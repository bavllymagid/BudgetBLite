package com.budget.b.lite.utils.exception.custom_exceptions;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException(String message){
        super(message);
    }
}
