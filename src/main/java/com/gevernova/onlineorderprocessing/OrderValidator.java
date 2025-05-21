package com.gevernova.onlineorderprocessing;

import java.util.function.Predicate;

public class OrderValidator {
    private final Predicate<Order> validPayment = o -> o.getPaymentMethod() != null;
    private final Predicate<Order> validAddress = o -> o.getDeliveryAddress() != null && !o.getDeliveryAddress().isEmpty();

    public void validate(Order order) throws InvalidPaymentException, InvalidAddressException {
        if (!validPayment.test(order)) throw new InvalidPaymentException("Invalid payment method.");
        if (!validAddress.test(order)) throw new InvalidAddressException("Invalid address.");
    }
}

