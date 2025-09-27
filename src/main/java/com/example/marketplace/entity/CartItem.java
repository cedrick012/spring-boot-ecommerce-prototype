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
	
	@NotNull(message = "Product must be provided to create a cartItem")
	@ManyToOne
	@JoinColumn(name="product_id")
	private Product product;
	
	@Min(value = 1, message = "Quantity must be greater than 0")
	private int quantity;
	
	@NotNull(message = "Cart must be provided to create a cartItem")
	@ManyToOne
	@JoinColumn(name="cart_id")
	@JsonBackReference
	private Cart cart;
}
