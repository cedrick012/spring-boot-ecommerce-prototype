package com.example.marketplace.service;

import java.util.UUID;

import com.example.marketplace.entity.Cart;

public interface CartService {
	Cart addProductToCart(UUID cartId, UUID productId);
	Cart getCart(UUID cartid);
	boolean checkout(UUID cartId);
}
