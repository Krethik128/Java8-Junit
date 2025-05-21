package com.gevernova.employeeleavetracker;

public class InvalidLeaveDateException extends RuntimeException {
    public InvalidLeaveDateException(String message) {
        super(message);
    }
}
