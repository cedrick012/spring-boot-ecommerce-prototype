package com.example.marketplace.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartRequest {
	@NotNull(message="Product id can not be null")
	private UUID productId;
	
	@Min(value = 1, message = "Quantity must be at least 1")
	@Max(value = 999, message = "Quantity cannot exceed 999")
	private int quantity = 1; // Default quantity is 1
}
