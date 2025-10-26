package com.example.marketplace.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * カート明細情報を管理するドメインモデルです。
 * 商品と数量の組み合わせを表し、カートとの関連を保持します。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
	/** カート明細ID */
	private Long id;
	
	/** 商品情報 */
	@NotNull(message = "カート項目を作成するには、商品が必要です。")
	private Product product;
	
	/** 数量 */
	@Min(value = 1, message = "数量は0より大きい値である必要があります。")
	private int quantity;
	
	/** 所属カート（JSON循環参照対策） */
	@NotNull(message = "カート項目を作成するには、カートが必要です。")
	@JsonBackReference
	private Cart cart;
}
