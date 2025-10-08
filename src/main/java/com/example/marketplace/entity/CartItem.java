package com.example.marketplace.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
	private Long id;
	
	@NotNull(message = "カート項目を作成するには、商品が必要です。")
	private Product product;
	
	@Min(value = 1, message = "数量は0より大きい値である必要があります。")
	private int quantity;
	
	@NotNull(message = "カート項目を作成するには、カートが必要です。")
	@JsonBackReference
	private Cart cart;
}
