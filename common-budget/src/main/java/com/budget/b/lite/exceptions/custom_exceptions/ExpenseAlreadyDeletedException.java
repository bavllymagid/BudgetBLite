package com.budget.b.lite.exceptions.custom_exceptions;

public class ExpenseAlreadyDeletedException extends RuntimeException{
    public ExpenseAlreadyDeletedException(String message) {
        super(message);
    }
}
