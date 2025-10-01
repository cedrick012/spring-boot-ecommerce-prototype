package com.example.marketplace.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddToCartRequest {
	@NotNull(message="商品IDはNULLにできません。")
	private UUID productId;
	
	@Min(value = 1, message = "数量は1以上である必要があります。")
	@Max(value = 999, message = "数量は999を超過することはできません。")
	private int quantity = 1; // Default quantity is 1
}
