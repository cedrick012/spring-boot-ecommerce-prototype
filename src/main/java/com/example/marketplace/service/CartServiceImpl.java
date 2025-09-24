package com.example.marketplace.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.marketplace.entity.Cart;
import com.example.marketplace.entity.CartItem;
import com.example.marketplace.entity.Product;
import com.example.marketplace.exception.NotFoundException;
import com.example.marketplace.repository.CartItemRepository;
import com.example.marketplace.repository.CartRepository;
import com.example.marketplace.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;
    
    @Override
    public Cart addProductToCart(UUID cartId, UUID productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new NotFoundException("Cart not found with ID: " + cartId));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));

        // Check if there's enough stock
        int currentQuantityInCart = cartItemRepository.findByCartAndProduct(cart, product)
            .map(CartItem::getQuantity)
            .orElse(0);
        
        if (currentQuantityInCart + quantity > product.getStock()) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + 
                (product.getStock() - currentQuantityInCart) + ", Requested: " + quantity);
        }

        // Find existing cart item or create new one
        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProduct(cart, product);

        if (existingItem.isPresent()) {
            // Update existing item quantity
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            // Create new cart item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }

        return cartRepository.findById(cartId)
            .orElseThrow(() -> new NotFoundException("Cart not found after update"));
    }

    @Override
    public Cart getOrCreateCartBySession(String sessionId) {
        return cartRepository.findBySessionId(sessionId)
        	    .stream()
        	    .findFirst()
        	    .orElseGet(() -> createNewCart(sessionId));
    }
    
    private Cart createNewCart(String sessionId) {
        Cart newCart = new Cart();
        newCart.setSessionId(sessionId);
        return cartRepository.save(newCart);
    }

    @Override
    public Cart getCart(UUID cartId) {
        if (cartId == null) {
            throw new IllegalArgumentException("Cart ID cannot be null");
        }
        return cartRepository.findById(cartId)
            .orElseThrow(() -> new NotFoundException("Cart not found with ID: " + cartId));
    }
    
    @Override
    public boolean checkout(UUID cartId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new NotFoundException("Cart not found with ID: " + cartId));
        cartRepository.delete(cart);
        return true;
    }
}
