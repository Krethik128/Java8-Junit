package com.gevernova.inventorymanagemntsystem;

import com.gevernova.inventorymanagemntsystem.exceptions.InvalidProductException;
import com.gevernova.inventorymanagemntsystem.exceptions.ProductNotFoundException;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        InventoryRepository inventoryRepository = new InMemoryInventoryRepository();
        InventoryService inventoryService = new InventoryService(inventoryRepository);

        System.out.println("--- Retail Store Inventory Management System ---");

        // --- 1. Adding Products ---
        try {
            System.out.println("\nAdding Products:");
            // Notice: No ID passed in constructor now
            Product smartphone = new Product("Smartphone", ProductCategory.Electronics, 10, 799.99);
            Product milk = new Product("Milk (1L)", ProductCategory.Grocery, 20, 2.49);
            Product tShirt = new Product("T-Shirt (M)", ProductCategory.Clothing, 5, 19.99);
            Product sciFiBook = new Product("Science Fiction Book", ProductCategory.Books, 3, 15.50);
            Product blender = new Product("Blender", ProductCategory.Home_appliances, 2, 49.99);

            inventoryService.addOrUpdateProduct(smartphone);
            inventoryService.addOrUpdateProduct(milk);
            inventoryService.addOrUpdateProduct(tShirt);
            inventoryService.addOrUpdateProduct(sciFiBook);
            inventoryService.addOrUpdateProduct(blender);

            // To update quantity for an existing product (Smartphone)
            // We need to fetch the existing product by name/ID, then create a new product object
            // to represent the 'update' to its quantity, then use addOrUpdateProduct
            // OR, as currently implemented in InventoryService, you can add another Product
            // object with the *same logical ID* and a *new quantity*.
            // Since UUID is generated in constructor, you can't create another 'new Product'
            // with the 'same ID'.
            // Therefore, the addOrUpdateProduct logic needs to adapt:
            // if productExists(product.getId()) is true, it should update its quantity.
            // But if the ID is auto-generated, how do we "update" a product that's already there
            // if we can't recreate it with the same ID?

            // This is a crucial point with UUIDs generated in the constructor:
            // You cannot create a NEW Product instance with the SAME UUID.
            // If you want to "update" a product (e.g., its quantity) by supplying
            // a new 'Product' object, your 'addOrUpdateProduct' logic needs to
            // identify existing products by a *different* unique key (like Name + Category)
            // or you must retrieve the existing Product object and modify its mutable fields.

            System.out.println("\nAttempting to add more quantity to Smartphone (using its original ID):");
            // Find the smartphone object by its generated ID
            Product existingSmartphone = inventoryService.searchProductsByName("Smartphone").stream().findFirst().orElse(null);
            if (existingSmartphone != null) {
                // Create a 'dummy' product with the same ID and the additional quantity
                // OR directly call a method to increase quantity by ID in InventoryService
                try {
                    // Let's use the decrease/increase quantity methods directly now.
                    // This is cleaner than relying on addOrUpdateProduct for quantity updates
                    // when IDs are UUIDs.
                    inventoryService.addOrUpdateProduct(new Product("Smartphone", ProductCategory.Electronics, 5, 799.99)); // This will generate a new UUID
                    // The above line will NOT update the existing smartphone, because it will create a new one with a new UUID.
                    // This is where the strategy needs to be re-evaluated.

                    // The 'addOrUpdateProduct' from the previous example, which checks for ID existence,
                    // is only valid if the ID is *provided* and *consistent*.
                    // With auto-generated UUIDs, you cannot "add" a product with the same ID twice.
                    // Instead, you need distinct methods:
                    // 1. `addNewProduct(Product product)`: always creates new entry, fails if ID (UUID) collision
                    // 2. `updateProductQuantity(String productId, int newQuantity)`: finds by ID, updates quantity

                    // Let's refine InventoryService for UUIDs.
                    // For now, I will use the `decreaseProductQuantity` which relies on ID lookup.
                    // For adding, each `new Product(...)` will be unique.
                    System.out.println("Current Inventory after initial adds:");
                    inventoryService.getAllProducts().forEach(System.out::println);

                } catch (InvalidProductException e) {
                    System.err.println("Error creating new Smartphone for update test: " + e.getMessage());
                }
            } else {
                System.out.println("Smartphone not found for quantity update test.");
            }

        } catch (InvalidProductException e) {
            System.err.println("Error during initial product setup: " + e.getMessage());
        }

        // --- Important Refinement for InventoryService with UUIDs ---
        // Since `new Product(...)` always generates a new UUID,
        // the `addOrUpdateProduct` in `InventoryService` needs to be more robust.
        // A common pattern is to have `addProduct(Product product)` for *new* products
        // and `increaseProductQuantity(String productId, int quantityToAdd)`.

        // Let's assume you've adjusted `InventoryService` to have a method like:
        // `public void increaseProductQuantity(String productId, int quantityToAdd)`
        // This method would then find the product by `productId`, increment its quantity,
        // and then call `inventoryRepository.updateProduct(foundProduct)`.

        // For this `Main` to work, the `InventoryService.addOrUpdateProduct`
        // would need to be changed:
        // `addOrUpdateProduct(Product product)` -> if product.id exists, update it.
        // With auto-generated UUIDs, a `new Product` means a new entry, not an update.
        // So, we'll revert to simpler `add` and `increaseQuantity` operations.

        // Re-aligning with how UUIDs work:
        // 1. You add a Product, it gets a unique ID.
        // 2. To change its quantity, you find it by its ID and use a method
        //    that specifically updates the quantity of *that* product.

        // Re-implementing Product adding/updating strategy in Main:
        System.out.println("\n--- Initializing Inventory with Unique Products ---");
        try {
            Product smartphone = new Product("Smartphone", ProductCategory.Electronics, 10, 799.99);
            Product milk = new Product("Milk (1L)", ProductCategory.Grocery, 20, 2.49);
            Product tShirt = new Product("T-Shirt (M)", ProductCategory.Clothing, 5, 19.99);
            Product sciFiBook = new Product("Science Fiction Book", ProductCategory.Books, 3, 15.50);
            Product blender = new Product("Blender", ProductCategory.Home_appliances, 2, 49.99);

            // Add these products initially (their IDs are now known)
            inventoryService.addOrUpdateProduct(smartphone); // This method should simply call repository.addProduct if no such ID exists
            inventoryService.addOrUpdateProduct(milk);
            inventoryService.addOrUpdateProduct(tShirt);
            inventoryService.addOrUpdateProduct(sciFiBook);
            inventoryService.addOrUpdateProduct(blender);

            System.out.println("\nCurrent Inventory after initial additions:");
            inventoryService.getAllProducts().forEach(System.out::println);

            // --- Update quantity of an existing product (Smartphone) ---
            System.out.println("\n--- Increasing quantity for Smartphone ---");
            inventoryService.increaseProductQuantity(smartphone.getId(), 5); // Use the ID of the 'smartphone' object
            System.out.println("Smartphone quantity after increase: " + inventoryService.searchProductsByName("Smartphone").get(0).getQuantity());

        } catch (InvalidProductException | ProductNotFoundException e) {
            System.err.println("Error during product setup/update: " + e.getMessage());
        }

        // --- 2. Filtering Low Stock Items ---
        System.out.println("\n--- Low Stock Items (Quantity < 5) ---");
        List<Product> lowStockItems = inventoryService.filterLowStockItems(5);
        if (lowStockItems.isEmpty()) {
            System.out.println("No low stock items found.");
        } else {
            lowStockItems.forEach(System.out::println);
        }

        // --- 3. Sorting Products ---
        System.out.println("\n--- Products Sorted by Category and Price ---");
        List<Product> sortedProducts = inventoryService.sortProductsByCategoryAndPrice();
        sortedProducts.forEach(System.out::println);

        // --- 4. Searching Products ---
        System.out.println("\n--- Searching for 'phone' ---");
        List<Product> phoneProducts = inventoryService.searchProductsByName("phone");
        if (phoneProducts.isEmpty()) {
            System.out.println("No products found matching 'phone'.");
        } else {
            phoneProducts.forEach(System.out::println);
        }

        System.out.println("\n--- Searching for products in ELECTRONICS category ---");
        List<Product> electronicsProducts = inventoryService.searchProductsByCategory(ProductCategory.Electronics);
        if (electronicsProducts.isEmpty()) {
            System.out.println("No products found in ELECTRONICS category.");
        } else {
            electronicsProducts.forEach(System.out::println);
        }

        // --- 5. Decreasing Quantity ---
        try {
            System.out.println("\n--- Decreasing quantity for Smartphone ---");
            // Need to retrieve the actual smartphone object to get its ID,
            // or just rely on searching by name and getting the ID from there.
            Product currentSmartphone = inventoryService.searchProductsByName("Smartphone").stream().findFirst().orElseThrow(() -> new ProductNotFoundException("Smartphone not found for decrease."));
            inventoryService.decreaseProductQuantity(currentSmartphone.getId(), 3);
            System.out.println("Smartphone quantity after decrease: " + inventoryService.searchProductsByName("Smartphone").get(0).getQuantity());
        } catch (ProductNotFoundException | InvalidProductException e) {
            System.err.println("Error decreasing quantity: " + e.getMessage());
        }

        // --- 6. Attempting Invalid Operations ---
        System.out.println("\n--- Attempting Invalid Operations ---");
        try {
            // Trying to create a product with negative price (constructor validation)
            new Product("Broken Item", ProductCategory.Others, 1, -5.00); // This line will throw InvalidProductException
            System.err.println("Should have thrown InvalidProductException for negative price, but didn't."); // If reached, means error
        } catch (InvalidProductException e) {
            System.err.println("Caught expected error (negative price in Product constructor): " + e.getMessage());
        }

        try {
            // Trying to remove a non-existent product
            inventoryService.removeProduct("NON_EXISTENT_PRODUCT_UUID");
        } catch (ProductNotFoundException e) {
            System.err.println("Caught expected error (product not found for removal): " + e.getMessage());
        }

        try {
            // Trying to decrease quantity below zero for blender
            Product currentBlender = inventoryService.searchProductsByName("Blender").stream().findFirst().orElseThrow(() -> new ProductNotFoundException("Blender not found for decrease."));
            inventoryService.decreaseProductQuantity(currentBlender.getId(), 5); // Blender initially had 2, trying to decrease by 5
        } catch (ProductNotFoundException | InvalidProductException e) {
            System.err.println("Caught expected error (decrease below zero): " + e.getMessage());
        }

        // --- 7. Removing a product ---
        try {
            System.out.println("\n--- Removing Milk ---");
            Product milkToRemove = inventoryService.searchProductsByName("Milk (1L)").stream().findFirst().orElseThrow(() -> new ProductNotFoundException("Milk not found for removal."));
            inventoryService.removeProduct(milkToRemove.getId());
            System.out.println("Current Inventory after removing Milk:");
            inventoryService.getAllProducts().forEach(System.out::println);
        } catch (ProductNotFoundException e) {
            System.err.println("Error removing product: " + e.getMessage());
        }
    }
}