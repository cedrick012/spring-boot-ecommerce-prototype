package com.example.marketplace.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class NotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        // Given
        String errorMessage = "The requested resource was not found.";

        // When
        NotFoundException exception = new NotFoundException(errorMessage);

        // Then
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }
}
