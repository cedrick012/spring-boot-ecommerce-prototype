package com.example.marketplace.entity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	private Long id;

	@NotBlank(message = "商品名は空欄にできません。")
	private String name;

	@Min(value = 1, message = "価格は0より大きい値である必要があります。")
	private double price;

	private String description;

	@Min(value = 0, message = "在庫をマイナスにすることはできません。")
	private int stock;
}
