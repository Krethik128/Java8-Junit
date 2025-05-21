package com.gevernova.onlineorderprocessing;

import java.util.List;
import java.util.Optional;

public class Order {
    private final String user;
    private final List<String> items;
    private final String paymentMethod;
    private final String deliveryAddress;
    private final Optional<String> promoCode;

    public Order(String user, List<String> items, String paymentMethod, String deliveryAddress, String promoCode) {
        //validates input
        if(user==null || user.isBlank() || items==null || items.isEmpty() || paymentMethod==null || paymentMethod.isBlank() || deliveryAddress==null || deliveryAddress.isBlank()){
            throw new IllegalArgumentException("User, items, payment method, delivery address and promo code cannot be null or blank");
        }
        this.user = user;
        this.items = items;
        this.paymentMethod = paymentMethod;
        this.deliveryAddress = deliveryAddress;
        this.promoCode = Optional.ofNullable(promoCode);
    }

    public String getUser() {
        return user;
    }

    public List<String> getItems() {
        return items;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public Optional<String> getPromoCode() {
        return promoCode;
    }
    // Getters
}

