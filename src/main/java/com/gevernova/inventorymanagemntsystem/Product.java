package com.gevernova.inventorymanagemntsystem;

import com.gevernova.inventorymanagemntsystem.exceptions.InvalidProductException;
import java.util.UUID;

public class Product {
    private final String id;
    private final String name;
    private final ProductCategory category;
    private int quantity;
    private double price;

    public Product(String name, ProductCategory category, int quantity, double price) throws InvalidProductException {
        if(name==null || name.isBlank() || category == null || quantity <= 0 || price <= 0){
            throw new InvalidProductException("Invalid product");
        }
        this.id = UUID.randomUUID().toString();// using a random number to Assign as id
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
    public void setQuantity(int quantity) throws InvalidProductException {
        if (quantity < 0) {
            throw new InvalidProductException("Quantity cannot be set to a negative value. Provided: " + quantity);
        }
        this.quantity = quantity;
    }
    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", price=" + String.format("%.2f", price) + // Format price for readability
                ", quantity=" + quantity +
                '}';
    }
}
