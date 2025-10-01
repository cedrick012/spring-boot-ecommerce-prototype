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
        String message = "ご注文が正常に完了しました。";

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
        String message = "在庫の問題により、チェックアウトに失敗しました。";
        List<String> errors = List.of("商品Aは在庫切れです。", "商品Bの在庫は残り2点です。");

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
        String message = "予期せぬエラーが発生しました。";

        // When
        CheckoutResult result = CheckoutResult.failure(message);

        // Then
        assertFalse(result.isSuccess());
        assertEquals(message, result.getMessage());
        assertNull(result.getErrors());
    }
}
