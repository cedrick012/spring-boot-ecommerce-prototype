package com.example.marketplace.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.marketplace.entity.Cart;
import com.example.marketplace.service.CartService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Cart getCart() {
        return cartService.getCart();
    }
    
    @PostMapping("/add/{productId}")
    public Cart addProductToCart(@PathVariable UUID productId) {
        return cartService.addProductToCart(productId);
    }
    
    @PostMapping("/checkout")
    public String checkout() {
        cartService.checkout();
        return "Checkout successful! Your order has been placed.";
    }

}