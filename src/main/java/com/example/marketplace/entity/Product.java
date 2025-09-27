package com.example.marketplace.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	@Id
	@UuidGenerator
	private UUID id;

	@NotBlank(message = "Product name cannot be blank")
	private String name;

	@Min(value = 1, message = "Price must be greater than 0")
	private double price;

	private String description;

	@Min(value = 0, message = "Stock cannot be negative")
	private int stock;
}
