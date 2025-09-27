package com.budget.b.lite.exceptions.custom_exceptions;

public class ExpenseChangeEmptyException extends RuntimeException{
    public ExpenseChangeEmptyException(String message) {
        super(message);
    }
}
