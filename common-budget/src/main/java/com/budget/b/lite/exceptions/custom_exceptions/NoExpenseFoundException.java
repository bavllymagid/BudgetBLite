package com.budget.b.lite.exceptions.custom_exceptions;

public class NoExpenseFoundException extends RuntimeException{
    public NoExpenseFoundException(String message) {
        super(message);
    }
}
