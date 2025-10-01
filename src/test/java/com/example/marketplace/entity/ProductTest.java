package com.example.marketplace.entity;

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

class ProductTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldBeValid_whenAllFieldsAreCorrect() {
        Product product = new Product(UUID.randomUUID(), "Valid Product", 99.99, "A valid description", 10);
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldBeInvalid_whenNameIsBlank() {
        Product product = new Product(UUID.randomUUID(), " ", 99.99, "A description", 10);
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("商品名は空欄にできません。")));
    }

    @Test
    void shouldBeInvalid_whenPriceIsZero() {
        Product product = new Product(UUID.randomUUID(), "Product", 0.0, "A description", 10);
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("価格は0より大きい値である必要があります。")));
    }

    @Test
    void shouldBeInvalid_whenStockIsNegative() {
        Product product = new Product(UUID.randomUUID(), "Product", 99.99, "A description", -1);
        Set<ConstraintViolation<Product>> violations = validator.validate(product);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("在庫をマイナスにすることはできません。")));
    }
}
