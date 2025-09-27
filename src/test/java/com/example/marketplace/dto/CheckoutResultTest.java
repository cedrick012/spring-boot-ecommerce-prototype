package com.example.marketplace.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class CheckoutResultTest {

    @Test
    void success_shouldCreateSuccessfulResult() {
        // Given
        String message = "Order placed successfully.";

        // When
        CheckoutResult result = CheckoutResult.success(message);

        // Then
        assertTrue(result.isSuccess());
        assertEquals(message, result.getMessage());
        assertNull(result.getErrors());
    }

    @Test
    void failure_withMessageAndErrors_shouldCreateFailedResult() {
        // Given
        String message = "Checkout failed due to stock issues.";
        List<String> errors = List.of("Item A is out of stock.", "Only 2 of Item B are available.");

        // When
        CheckoutResult result = CheckoutResult.failure(message, errors);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(message, result.getMessage());
        assertEquals(errors, result.getErrors());
    }

    @Test
    void failure_withMessageOnly_shouldCreateFailedResult() {
        // Given
        String message = "An unexpected error occurred.";

        // When
        CheckoutResult result = CheckoutResult.failure(message);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(message, result.getMessage());
        assertNull(result.getErrors());
    }
}
