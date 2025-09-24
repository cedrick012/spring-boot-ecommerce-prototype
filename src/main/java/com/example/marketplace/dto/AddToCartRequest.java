package com.example.marketplace.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartRequest {
	@NotNull(message="Product id can not be null")
	private UUID productId;
}
