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

	@NotBlank(message = "商品名は空欄にできません。")
	private String name;

	@Min(value = 1, message = "価格は0より大きい値である必要があります。")
	private double price;

	private String description;

	@Min(value = 0, message = "在庫をマイナスにすることはできません。")
	private int stock;
}
