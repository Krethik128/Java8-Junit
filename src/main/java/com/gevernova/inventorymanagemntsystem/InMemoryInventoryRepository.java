package com.gevernova.inventorymanagemntsystem;

import com.gevernova.inventorymanagemntsystem.exceptions.*;
import java.util.*;

public class InMemoryInventoryRepository implements InventoryRepository {
    // Key: Product ID, Value: Product object
    private final Map<String, Product> products = new HashMap<>();

    @Override
    public void addProduct(Product product) {
        if (products.containsKey(product.getId())) {
            throw new ProductAlreadyExists("Product with ID " + product.getId() + " already exists.");
        }
        else{
            products.put(product.getId(), product);
        }
    }

    @Override
    public void updateProduct(Product product) throws ProductNotFoundException {
        if (!products.containsKey(product.getId())) {
            throw new ProductNotFoundException("Product with ID " + product.getId() + " not found for update.");
        }
        products.put(product.getId(), product); // Overwrites the existing product with the updated one
    }

    @Override
    public void removeProduct(String productId) throws ProductNotFoundException {
        if (!products.containsKey(productId)) {
            throw new ProductNotFoundException("Product with ID " + productId + " not found for removal.");
        }
        else{
            products.remove(productId);
            System.out.println("Removed product with ID: " + productId);
        }
    }

    @Override
    public Optional<Product> findProductById(String productId) {
        return Optional.ofNullable(products.get(productId));
    }

    @Override
    public List<Product> findAllProducts() {
        return new ArrayList<>(products.values()); // Return a copy to prevent external modification of the internal list
    }

    @Override
    public boolean productExists(String productId) {
        return products.containsKey(productId);
    }
}
