package com.example.marketplace.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.marketplace.dto.AddToCartRequest;
import com.example.marketplace.dto.CheckoutResult;
import com.example.marketplace.entity.Cart;
import com.example.marketplace.service.CartService;

import jakarta.servlet.http.HttpSession;
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
        return cartService.addProductToCart(id, bodyDto.getProductId(), bodyDto.getQuantity());
    }
    
    @PostMapping("/session/add-product")  
    public Cart addProductToCartBySession(
            HttpSession session,
            @Valid @RequestBody AddToCartRequest bodyDto) {
        String sessionId = session.getId();
        Cart cart = cartService.getOrCreateCartBySession(sessionId);
        return cartService.addProductToCart(cart.getId(), bodyDto.getProductId(), bodyDto.getQuantity());
    }
    
    @GetMapping("/session")
    public Cart getCartBySession(HttpSession session) {
        String sessionId = session.getId();
        return cartService.getOrCreateCartBySession(sessionId);
    }
    
    @DeleteMapping("/{id}/checkout")
    public ResponseEntity<?> checkout(@PathVariable UUID id) {
        CheckoutResult result = cartService.checkout(id);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

}