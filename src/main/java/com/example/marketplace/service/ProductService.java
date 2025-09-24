package com.example.marketplace.service;

import java.util.List;
import java.util.UUID;

import com.example.marketplace.entity.Product;

public interface ProductService {
	List<Product> getAllProducts();
	Product findById(UUID id);
}
