import com.gevernova.onlineorderprocessing.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OrderValidatorTest {

    private OrderValidator orderValidator;

    @BeforeEach
    void setUp() {
        orderValidator = new OrderValidator();
    }

    @Test
    @DisplayName("Should validate a valid order successfully")
    void shouldValidateValidOrderSuccessfully() {
        List<String> items = Arrays.asList("Item A", "Item B");
        Order validOrder = new Order("testUser", items, "Credit Card", "123 Test St", "PROMO123");
        assertDoesNotThrow(() -> orderValidator.validate(validOrder));
    }

    @Test
    @DisplayName("Should throw InvalidPaymentException for null payment method")
    void shouldThrowInvalidPaymentExceptionForNullPaymentMethod() {
        List<String> items = Arrays.asList("Item C");
        Order orderWithNullPayment = new Order("testUser", items, null, "456 Test Ave", null);
        InvalidPaymentException thrown = assertThrows(InvalidPaymentException.class, () ->
                orderValidator.validate(orderWithNullPayment));
        assertEquals("Invalid payment method.", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidAddressException for null delivery address")
    void shouldThrowInvalidAddressExceptionForNullDeliveryAddress() {
        List<String> items = Arrays.asList("Item D");
        Order orderWithNullAddress = new Order("testUser", items, "PayPal", null, null);
        InvalidAddressException thrown = assertThrows(InvalidAddressException.class, () ->
                orderValidator.validate(orderWithNullAddress));
        assertEquals("Invalid address.", thrown.getMessage());
    }

    @Test
    @DisplayName("Should throw InvalidAddressException for empty delivery address")
    void shouldThrowInvalidAddressExceptionForEmptyDeliveryAddress() {
        List<String> items = Arrays.asList("Item E");
        Order orderWithEmptyAddress = new Order("testUser", items, "Debit Card", "", null);
        InvalidAddressException thrown = assertThrows(InvalidAddressException.class, () ->
                orderValidator.validate(orderWithEmptyAddress));
        assertEquals("Invalid address.", thrown.getMessage());
    }
    @Test
    @DisplayName("Should throw IllegalArgumentException if user is null during Order creation")
    void shouldThrowIllegalArgumentExceptionIfUserIsNullOnOrderCreation() {
        List<String> items = Arrays.asList("Item F");
        assertThrows(IllegalArgumentException.class, () ->
                new Order(null, items, "Cash", "789 Test Rd", null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if user is empty during Order creation")
    void shouldThrowIllegalArgumentExceptionIfUserIsEmptyOnOrderCreation() {
        List<String> items = Arrays.asList("Item G");
        assertThrows(IllegalArgumentException.class, () ->
                new Order("", items, "Cash", "789 Test Rd", null));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException if items list is null during Order creation")
    void shouldThrowIllegalArgumentExceptionIfItemsIsNullOnOrderCreation() {
        assertThrows(IllegalArgumentException.class, () ->
                new Order("testUser", null, "Credit Card", "123 Test St", null));
    }

    @Test
    @DisplayName("Should successfully validate order with empty items list")
    void shouldValidateOrderWithEmptyItemsList() {
        List<String> items = Collections.emptyList();
        Order orderWithEmptyItems = new Order("testUser", items, "Credit Card", "123 Test St", null);
        assertDoesNotThrow(() -> orderValidator.validate(orderWithEmptyItems));
    }

    @Test
    @DisplayName("Should correctly handle promo code as Optional.empty() when null")
    void shouldHandlePromoCodeAsOptionalEmptyWhenNull() {
        List<String> items = Collections.singletonList("Single Item");
        Order order = new Order("user123", items, "Credit Card", "123 Main St", null);
        assertFalse(order.getPromoCode().isPresent());
        assertEquals(Optional.empty(), order.getPromoCode());
    }

    @Test
    @DisplayName("Should correctly handle promo code as Optional.of() when not null")
    void shouldHandlePromoCodeAsOptionalOfWhenNotNull() {
        List<String> items = Collections.singletonList("Another Item");
        String promo = "SUMMER20";
        Order order = new Order("user456", items, "Debit Card", "456 Oak Ave", promo);
        assertTrue(order.getPromoCode().isPresent());
        assertEquals(promo, order.getPromoCode().get());
        assertEquals(Optional.of(promo), order.getPromoCode());
    }
}
