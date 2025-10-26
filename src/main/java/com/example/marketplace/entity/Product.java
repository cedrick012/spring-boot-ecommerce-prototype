package com.example.marketplace.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 商品情報を管理するドメインモデルです。
 * 商品名、価格、在庫数などの基本情報を保持します。
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	/** 商品ID */
	private Long id;

	/** 商品名 */
	@NotBlank(message = "商品名は空欄にできません。")
	private String name;

	/** 商品価格 */
	@Min(value = 1, message = "価格は0より大きい値である必要があります。")
	private double price;

	/** 商品説明 */
	private String description;

	/** 在庫数 */
	@Min(value = 0, message = "在庫をマイナスにすることはできません。")
	private int stock;
}
