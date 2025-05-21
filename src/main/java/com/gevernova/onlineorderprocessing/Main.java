package com.gevernova.onlineorderprocessing;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- Online Order Processing System ---");

        OrderValidator validator = new OrderValidator();

        // --- Scenario 1: Valid Order ---
        System.out.println("\n--- Valid Order Scenario ---");
        List<String> items1 = Arrays.asList("Laptop", "Mouse", "Keyboard");
        Order order1 = new Order("user123", items1, "Credit Card", "123 Main St, Anytown", "SAVE10");
        try {
            System.out.println("Attempting to validate Order 1:");
            System.out.println("User: " + order1.getUser());
            System.out.println("Items: " + order1.getItems());
            System.out.println("Payment Method: " + order1.getPaymentMethod());
            System.out.println("Delivery Address: " + order1.getDeliveryAddress());
            System.out.println("Promo Code: " + order1.getPromoCode().orElse("N/A"));

            validator.validate(order1);
            System.out.println("Order 1 validated successfully!");
        } catch (InvalidPaymentException | InvalidAddressException e) {
            System.err.println("Validation Error for Order 1: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Initialization Error for Order 1: " + e.getMessage());
        }

        // --- Scenario 2: Order with Missing Payment Method ---
        System.out.println("\n--- Missing Payment Method Scenario ---");
        List<String> items2 = Arrays.asList("Book", "Pen");
        Order order2 = new Order("user456", items2, null, "456 Oak Ave, Othercity", null);
        try {
            System.out.println("Attempting to validate Order 2 (null payment method):");
            System.out.println("User: " + order2.getUser());
            System.out.println("Items: " + order2.getItems());
            System.out.println("Payment Method: " + order2.getPaymentMethod());
            System.out.println("Delivery Address: " + order2.getDeliveryAddress());
            System.out.println("Promo Code: " + order2.getPromoCode().orElse("N/A"));

            validator.validate(order2);
            System.out.println("Order 2 validated successfully!");
        } catch (InvalidPaymentException | InvalidAddressException e) {
            System.err.println("Validation Error for Order 2: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Initialization Error for Order 2: " + e.getMessage());
        }

        // --- Scenario 3: Order with Empty Delivery Address ---
        System.out.println("\n--- Empty Delivery Address Scenario ---");
        List<String> items3 = Collections.singletonList("Coffee Maker");
        Order order3 = new Order("user789", items3, "PayPal", "", "FREESHIP");
        try {
            System.out.println("Attempting to validate Order 3 (empty delivery address):");
            System.out.println("User: " + order3.getUser());
            System.out.println("Items: " + order3.getItems());
            System.out.println("Payment Method: " + order3.getPaymentMethod());
            System.out.println("Delivery Address: " + order3.getDeliveryAddress());
            System.out.println("Promo Code: " + order3.getPromoCode().orElse("N/A"));

            validator.validate(order3);
            System.out.println("Order 3 validated successfully!");
        } catch (InvalidPaymentException | InvalidAddressException e) {
            System.err.println("Validation Error for Order 3: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Initialization Error for Order 3: " + e.getMessage());
        }

        // --- Scenario 4: Order with null items (should throw IllegalArgumentException during Order creation) ---
        System.out.println("\n--- Null Items Scenario (Order creation) ---");
        try {
            System.out.println("Attempting to create Order 4 (null items list):");
            new Order("userABC", null, "Debit Card", "789 Pine Rd, Nowhere", null);
            System.out.println("Order 4 created successfully (unexpected)!");
        } catch (IllegalArgumentException e) {
            System.err.println("Expected Initialization Error for Order 4: " + e.getMessage());
        }

        // --- Scenario 5: Order with empty items list (should be valid for validation) ---
        System.out.println("\n--- Empty Items List Scenario ---");
        List<String> items5 = Collections.emptyList();
        Order order5 = new Order("userXYZ", items5, "Bank Transfer", "101 Elm St, Villageton", "DISCOUNT");
        try {
            System.out.println("Attempting to validate Order 5 (empty items list):");
            System.out.println("User: " + order5.getUser());
            System.out.println("Items: " + order5.getItems());
            System.out.println("Payment Method: " + order5.getPaymentMethod());
            System.out.println("Delivery Address: " + order5.getDeliveryAddress());
            System.out.println("Promo Code: " + order5.getPromoCode().orElse("N/A"));
            validator.validate(order5);
            System.out.println("Order 5 validated successfully!");
        } catch (InvalidPaymentException | InvalidAddressException e) {
            System.err.println("Validation Error for Order 5: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Initialization Error for Order 5: " + e.getMessage());
        }

        // --- Scenario 6: Order with null user (should throw IllegalArgumentException during Order creation) ---
        System.out.println("\n--- Null User Scenario (Order creation) ---");
        try {
            System.out.println("Attempting to create Order 6 (null user):");
            new Order(null, Arrays.asList("Chair"), "Cash", "555 Market St, Cityville", null);
            System.out.println("Order 6 created successfully (unexpected)!");
        } catch (IllegalArgumentException e) {
            System.err.println("Expected Initialization Error for Order 6: " + e.getMessage());
        }
    }
}
