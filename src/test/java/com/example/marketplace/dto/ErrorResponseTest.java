package com.example.marketplace.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import org.junit.jupiter.api.Test;

class ErrorResponseTest {

    @Test
    void testErrorResponseCreation() {
        // Given
        int status = 404;
        String message = "リソースが見つかりません。";
        Object details = Map.of("id", "123", "error", "要求されたリソースは存在しません。");

        // When
        ErrorResponse errorResponse = new ErrorResponse(status, message, details);

        // Then
        assertEquals(status, errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(details, errorResponse.getDetails());
    }
}
