package com.gevernova.inventorymanagemntsystem;

import com.gevernova.inventorymanagemntsystem.exceptions.InvalidProductException;
import com.gevernova.inventorymanagemntsystem.exceptions.ProductNotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class InventoryService {

    private final InventoryRepository inventoryRepository;

    // Constructor Injection for dependency
    /**
     * Dependency Injection: It takes an InventoryRepository object in its constructor.
     * This is called Constructor Injection, and it's a key principle of good design:
     */
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    // --- CRUD Operations ---
    /**
     * Adds a new product or updates the quantity if the product already exists.
     * @param product The product to add.
     * @throws InvalidProductException If product details are invalid (e.g., negative quantity, price).
     */
    public void addOrUpdateProduct(Product product) throws InvalidProductException {
        // This product.getId() will be a NEW UUID for each new Product object
        // if you instantiate 'new Product("Smartphone", ...)' repeatedly.
        if (inventoryRepository.productExists(product.getId())) {
            // This path will only be taken if you somehow *re-used* an existing Product object reference
            // or manually set its UUID (which we are not doing with UUID.randomUUID()).
            // For auto-generated UUIDs, this `if` is essentially for internal updates of an already-added object.
            Product existingProduct = inventoryRepository.findProductById(product.getId()).get();
            existingProduct.setQuantity(existingProduct.getQuantity() + product.getQuantity()); // Add quantities
            try {
                inventoryRepository.updateProduct(existingProduct);
                System.out.println("Updated quantity for product: " + existingProduct.getName() + " to " + existingProduct.getQuantity());
            } catch (ProductNotFoundException e) {
                throw new RuntimeException("Internal error: Product disappeared during update.", e);
            }
        } else {
            // This is the common path when a new Product object (with new UUID) is passed.
            inventoryRepository.addProduct(product);
            System.out.println("Added new product: " + product.getName() + " with quantity " + product.getQuantity());
        }
    }
    /**
     * Adds a brand-new product to the inventory.
     * This method assumes the product being added has a unique ID (UUID generated in its constructor).
     *
     * @param product The product to add.
     * @throws InvalidProductException If product details are invalid (e.g., negative quantity, price).
     * (Note: most of this validation is now in the Product constructor itself).
     */
    public void addNewProduct(Product product) throws InvalidProductException {
        if (inventoryRepository.productExists(product.getId())) {
            throw new InvalidProductException("A product with this ID already exists. Cannot add as new.");
        }
        inventoryRepository.addProduct(product);
        System.out.println("Added new product: " + product.getName() + " (ID: " + product.getId() + ") with quantity " + product.getQuantity());
    }

    /**
     * Removes a product from the inventory.
     * @param productId The ID of the product to remove.
     * @throws ProductNotFoundException If the product does not exist.
     */
    public void removeProduct(String productId) throws ProductNotFoundException {
        inventoryRepository.removeProduct(productId);
        System.out.println("Removed product with ID: " + productId);
    }

    public void increaseProductQuantity(String productId, int quantityToAdd) throws ProductNotFoundException, InvalidProductException {
        if (quantityToAdd <= 0) {
            throw new InvalidProductException("Quantity to add must be positive. Provided: " + quantityToAdd);
        }
        Product product = inventoryRepository.findProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found for quantity increase."));

        product.setQuantity(product.getQuantity() + quantityToAdd);
        inventoryRepository.updateProduct(product);
        System.out.println("Increased quantity for product: " + product.getName() + " to " + product.getQuantity());
    }
    /**
     * Decreases the quantity of a product.
     * @param productId The ID of the product.
     * @param quantityToDecrease The amount to decrease.
     * @throws ProductNotFoundException If the product does not exist.
     * @throws InvalidProductException If the quantity to decrease is negative or results in negative stock.
     */
    public void decreaseProductQuantity(String productId, int quantityToDecrease) throws ProductNotFoundException, InvalidProductException {
        if (quantityToDecrease < 0) {
            throw new InvalidProductException("Quantity to decrease cannot be negative.");
        }
        Product product = inventoryRepository.findProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found."));

        if (product.getQuantity() < quantityToDecrease) {
            throw new InvalidProductException("Cannot decrease quantity below zero. Current: " + product.getQuantity() + ", Decrease: " + quantityToDecrease);
        }
        product.setQuantity(product.getQuantity() - quantityToDecrease);
        inventoryRepository.updateProduct(product);
        System.out.println("Decreased quantity for product: " + product.getName() + " to " + product.getQuantity());
    }

    // --- Search and Filter Operations using Java 8 Streams ---

    /**
     * Searches for products by name (case-insensitive, partial match).
     * @param name The name to search for.
     * @return A list of matching products.
     */
    public List<Product> searchProductsByName(String name) {
        return inventoryRepository.findAllProducts().stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    /**
     * Searches for products by category.
     * @param category The category to search for.
     * @return A list of matching products.
     */
    public List<Product> searchProductsByCategory(ProductCategory category) {
        return inventoryRepository.findAllProducts().stream()
                .filter(p -> p.getCategory() == category)
                .collect(Collectors.toList());
    }

    /**
     * Filters products with quantity less than the specified threshold.
     * @param threshold The quantity threshold.
     * @return A list of low-stock products.
     */
    public List<Product> filterLowStockItems(int threshold) {
        if (threshold < 0) {
            // While not an InvalidProductException, it's an invalid service parameter
            throw new IllegalArgumentException("Threshold cannot be negative.");
        }
        return inventoryRepository.findAllProducts().stream()
                .filter(p -> p.getQuantity() < threshold)
                .collect(Collectors.toList());
    }

    /**
     * Sorts all products by category, then by price (ascending).
     * @return A sorted list of products.
     */
    public List<Product> sortProductsByCategoryAndPrice() {
        return inventoryRepository.findAllProducts().stream()
                .sorted(Comparator
                        .comparing(Product::getCategory)
                        .thenComparing(Product::getPrice))
                .collect(Collectors.toList());
    }

    // --- Utility Method ---
    public List<Product> getAllProducts() {
        return inventoryRepository.findAllProducts();
    }
}
