package com.budget.b.lite.utils.exceptions.custom_exceptions;

public class ExpenseAlreadyDeletedException extends RuntimeException{
    public ExpenseAlreadyDeletedException(String message) {
        super(message);
    }
}
