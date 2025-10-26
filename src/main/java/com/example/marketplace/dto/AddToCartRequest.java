package com.example.marketplace.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * カートに商品を追加する際のリクエストデータです。
 * 商品IDと数量の情報を保持します。
 */
@Data
public class AddToCartRequest {
	/** 追加対象の商品ID */
	@NotNull(message="商品IDはNULLにできません。")
	private Long productId;
	
	/** 追加数量（デフォルト値: 1） */
	@Min(value = 1, message = "数量は1以上である必要があります。")
	@Max(value = 999, message = "数量は999を超過することはできません。")
	private int quantity = 1;
}
