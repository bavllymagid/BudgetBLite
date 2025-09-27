package com.budget.b.lite.exceptions.custom_exceptions;

public class CategoryExistsException extends RuntimeException{
    public CategoryExistsException(String message) {
        super(message);
    }
}
