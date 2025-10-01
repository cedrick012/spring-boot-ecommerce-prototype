package com.example.marketplace.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class NotFoundExceptionTest {

    @Test
    void testConstructorWithMessage() {
        // Given
        String errorMessage = "要求されたリソースが見つかりませんでした。";

        // When
        NotFoundException exception = new NotFoundException(errorMessage);

        // Then
        assertEquals(errorMessage, exception.getMessage());
        assertNull(exception.getCause());
    }
}
