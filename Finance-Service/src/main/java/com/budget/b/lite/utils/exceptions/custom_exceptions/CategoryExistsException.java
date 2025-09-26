package com.budget.b.lite.utils.exceptions.custom_exceptions;

public class CategoryExistsException extends RuntimeException{
    public CategoryExistsException(String message) {
        super(message);
    }
}
