package com.gevernova.inventorymanagemntsystem;

import com.gevernova.inventorymanagemntsystem.exceptions.ProductNotFoundException;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository {
    void addProduct(Product product);
    void removeProduct(String productId) throws ProductNotFoundException;
    void updateProduct(Product product) throws ProductNotFoundException;
    Optional<Product> findProductById(String productId);
    List<Product> findAllProducts();
    boolean productExists(String productId);
}
