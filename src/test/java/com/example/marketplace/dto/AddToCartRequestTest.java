package com.example.marketplace.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AddToCartRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldBeValid_whenAllFieldsAreCorrect() {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(UUID.randomUUID());
        request.setQuantity(10);

        Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldHaveDefaultQuantityOfOne() {
        AddToCartRequest request = new AddToCartRequest();
        assertEquals(1, request.getQuantity());
    }

    @Test
    void shouldBeInvalid_whenProductIdIsNull() {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(null);

        Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Product id can not be null")));
    }

    @Test
    void shouldBeInvalid_whenQuantityIsZero() {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(UUID.randomUUID());
        request.setQuantity(0);

        Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Quantity must be at least 1")));
    }

    @Test
    void shouldBeInvalid_whenQuantityIsTooLarge() {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(UUID.randomUUID());
        request.setQuantity(1000);

        Set<ConstraintViolation<AddToCartRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Quantity cannot exceed 999")));
    }
}
