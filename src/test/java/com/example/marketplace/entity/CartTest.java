package com.example.marketplace.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

class CartTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldBeValid_whenSessionIdIsPresent() {
        Cart cart = new Cart();
        cart.setId(UUID.randomUUID());
        cart.setSessionId("valid-session-id");

        Set<ConstraintViolation<Cart>> violations = validator.validate(cart);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeInvalid_whenSessionIdIsBlank() {
        Cart cart = new Cart();
        cart.setId(UUID.randomUUID());
        cart.setSessionId(" ");

        Set<ConstraintViolation<Cart>> violations = validator.validate(cart);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("セッションIDは空欄にできません。")));
    }

    @Test
    void shouldCorrectlyAddItem() {
        Cart cart = new Cart();
        cart.setSessionId("session-1");
        Product product = new Product(UUID.randomUUID(), "Test Product", 10.0, "Desc", 10);
        CartItem item = new CartItem(UUID.randomUUID(), product, 1, cart);

        cart.getItems().add(item);

        assertEquals(1, cart.getItems().size());
        assertTrue(cart.getItems().contains(item));
    }
}
