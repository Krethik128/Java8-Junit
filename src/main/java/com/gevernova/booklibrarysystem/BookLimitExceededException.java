package com.gevernova.booklibrarysystem;

public class BookLimitExceededException extends RuntimeException {
    public BookLimitExceededException(String message) {
        super(message);
    }
}
