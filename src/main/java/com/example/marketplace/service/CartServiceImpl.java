package com.example.marketplace.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.marketplace.entity.Cart;
import com.example.marketplace.entity.CartItem;
import com.example.marketplace.entity.Product;
import com.example.marketplace.exception.NotFoundException;
import com.example.marketplace.repository.CartRepository;
import com.example.marketplace.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    
    @Override
    public Cart addProductToCart(UUID cartId, UUID productId) {
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new NotFoundException("Cart not found with ID: " + cartId));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + 1);
        } else {
            CartItem newItem = new CartItem(null, product, 1, cart);
            cart.getItems().add(newItem);
        }

        return cartRepository.save(cart);
    }

    @Override
    public Cart getCart(UUID cartId) {
        return getOrCreateCart(cartId);
    }
    
    @Override
    public boolean checkout(UUID cartId) {
        Cart cart = getOrCreateCart(cartId);
        cartRepository.delete(cart);
        return true;
    }
    
    private Cart getOrCreateCart(UUID cartId) {
        if (cartId == null) {
            Cart newCart = new Cart();
            cartRepository.save(newCart);
            return newCart;
        }
        
        return cartRepository.findById(cartId).orElseThrow(() -> new NotFoundException("Cart not found with ID: " + cartId));
    }

}
