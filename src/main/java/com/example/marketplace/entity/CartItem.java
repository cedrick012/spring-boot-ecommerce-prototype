package com.example.marketplace.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {
	@Id
	@UuidGenerator
	private UUID id;
	
	@NotNull(message = "カート項目を作成するには、商品が必要です。")
	@ManyToOne
	@JoinColumn(name="product_id")
	private Product product;
	
	@Min(value = 1, message = "数量は0より大きい値である必要があります。")
	private int quantity;
	
	@NotNull(message = "カート項目を作成するには、カートが必要です。")
	@ManyToOne
	@JoinColumn(name="cart_id")
	@JsonBackReference
	private Cart cart;
}
