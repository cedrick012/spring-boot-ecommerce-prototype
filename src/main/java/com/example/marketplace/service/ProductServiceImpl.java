package com.example.marketplace.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.marketplace.entity.Product;
import com.example.marketplace.exception.NotFoundException;
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

	@Override
	public Product findById(UUID id) {
		return productRepository.findById(id)
			.orElseThrow(() -> new NotFoundException(id + " の商品が見つかりません。"));
	}

	@Override
	public void reduceStock(UUID productId, int quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("削減する数量は、0より大きい値である必要があります。");
		}
		
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new NotFoundException(productId + " の商品が見つかりません。"));
		
		if (product.getStock() < quantity) {
			throw new IllegalArgumentException("在庫不足です。在庫数: " + product.getStock() + ", ご要望数: " + quantity);
		}
		
		product.setStock(product.getStock() - quantity);
		productRepository.save(product);
	}

	@Override
	public void reduceStock(UUID productId, int quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("Quantity to reduce must be greater than 0");
		}
		
		Product product = productRepository.findById(productId)
			.orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));
		
		if (product.getStock() < quantity) {
			throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStock() + 
				", Requested: " + quantity);
		}
		
		product.setStock(product.getStock() - quantity);
		productRepository.save(product);
	}
}
