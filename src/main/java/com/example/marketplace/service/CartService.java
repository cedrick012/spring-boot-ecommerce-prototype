package com.example.marketplace.service;

import java.util.UUID;

import com.example.marketplace.dto.CheckoutResult;
import com.example.marketplace.entity.Cart;

public interface CartService {
	Cart addProductToCart(UUID cartId, UUID productId, int quantity);
	Cart getCart(UUID cartid);
	Cart getOrCreateCartBySession(String sessionId);
	CheckoutResult checkout(UUID cartId);
}
