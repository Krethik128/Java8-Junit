package com.gevernova.inventorymanagemntsystem.exceptions;

public class ProductAlreadyExists extends RuntimeException {
    public ProductAlreadyExists(String message) {
        super(message);
    }
}
