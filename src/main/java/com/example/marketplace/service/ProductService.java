package com.example.marketplace.service;

import java.util.List;

import com.example.marketplace.entity.Product;

public interface ProductService {
	List<Product> getAllProducts();
	Product findById(Long id);
	void reduceStock(Long productId, int quantity);
}
