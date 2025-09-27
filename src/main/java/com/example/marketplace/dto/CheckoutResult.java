package com.example.marketplace.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckoutResult {
    private boolean success;
    private String message;
    private List<String> errors;
    
    public static CheckoutResult success(String message) {
        return new CheckoutResult(true, message, null);
    }
    
    public static CheckoutResult failure(String message, List<String> errors) {
        return new CheckoutResult(false, message, errors);
    }
    
    public static CheckoutResult failure(String message) {
        return new CheckoutResult(false, message, null);
    }
}
