package com.example.marketplace.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
	
	@ManyToOne
	@JoinColumn(name="product_id")
	private Product product;
	
	private int quantity;
	
	@ManyToOne
	@JoinColumn(name="cart_id")
	@JsonBackReference
	private Cart cart;
}
