package com.example.marketplace.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CartItemTest {

    private Validator validator;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        cart = new Cart();
        cart.setId(UUID.randomUUID());
        cart.setSessionId("test-session");

        product = new Product(UUID.randomUUID(), "Test Product", 10.99, "A description", 100);
    }

    @Test
    void shouldBeValid_whenAllFieldsAreCorrect() {
        CartItem item = new CartItem(UUID.randomUUID(), product, 5, cart);
        Set<ConstraintViolation<CartItem>> violations = validator.validate(item);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeInvalid_whenCartIsNull() {
        CartItem item = new CartItem(UUID.randomUUID(), product, 1, null);
        Set<ConstraintViolation<CartItem>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Cart must be provided to create a cartItem")));
    }

    @Test
    void shouldBeInvalid_whenProductIsNull() {
        CartItem item = new CartItem(UUID.randomUUID(), null, 1, cart);
        Set<ConstraintViolation<CartItem>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Product must be provided to create a cartItem")));
    }

    @Test
    void shouldBeInvalid_whenQuantityIsZero() {
        CartItem item = new CartItem(UUID.randomUUID(), product, 0, cart);
        Set<ConstraintViolation<CartItem>> violations = validator.validate(item);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Quantity must be greater than 0")));
    }
}
