package com.example.marketplace.service;

import com.example.marketplace.dto.CheckoutResult;
import com.example.marketplace.entity.Cart;

public interface CartService {
	Cart addProductToCart(Long cartId, Long productId, int quantity);
	Cart getCart(Long cartid);
	Cart getOrCreateCartBySession(String sessionId);
	CheckoutResult checkout(Long cartId);
}
