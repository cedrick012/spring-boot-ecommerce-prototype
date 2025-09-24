package com.example.marketplace.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.marketplace.dto.AddToCartRequest;
import com.example.marketplace.entity.Cart;
import com.example.marketplace.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {
	
    private final CartService cartService;

    @GetMapping("/{id}")
    public Cart getCart(@PathVariable UUID id) {
        return cartService.getCart(id);
    }
    
    @PostMapping
    public Cart createCart() {
    	return cartService.getCart(null);
    }
    
    @PostMapping("/{id}/add-product")
    public Cart addProductToCart(
    		@PathVariable UUID id,
    		@Valid @RequestBody AddToCartRequest bodyDto) {
        return cartService.addProductToCart(id, bodyDto.getProductId());
    }
    
    @DeleteMapping("/{id}/checkout")
    public String checkout(@PathVariable UUID id) {
        cartService.checkout(id);
        return "Checkout successful! Your order has been placed.";
    }

}