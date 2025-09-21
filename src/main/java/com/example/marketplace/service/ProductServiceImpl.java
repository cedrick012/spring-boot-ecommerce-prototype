package com.example.marketplace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.marketplace.entity.Product;
import com.example.marketplace.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	
	private final ProductRepository productRepository;

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}
}
