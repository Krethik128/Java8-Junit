package com.gevernova.employeeleavetracker;

public class LeaveLimitExceededException extends RuntimeException {
    public LeaveLimitExceededException(String message) {
        super(message);
    }
}
