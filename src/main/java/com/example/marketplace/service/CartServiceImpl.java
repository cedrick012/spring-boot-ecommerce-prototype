package com.example.marketplace.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.marketplace.entity.Cart;
import com.example.marketplace.entity.CartItem;
import com.example.marketplace.entity.Product;
import com.example.marketplace.repository.CartRepository;
import com.example.marketplace.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    
    private Cart getOrCreateCart(UUID cartId) {
        if (cartId == null) {
            Cart newCart = new Cart();
            cartRepository.save(newCart);
            return newCart;
        }
        return cartRepository.findById(cartId).orElseThrow(() -> new RuntimeException("Cart not found"));
    }

        return cartRepository.findById(cartId)
            .orElseThrow(() -> new RuntimeException("Cart not found with ID: " + cartId));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + productId));
                .orElseThrow(() -> new RuntimeException("Product not found"));

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

    public Cart getCart(UUID cartId) {
        return getOrCreateCart(cartId);
    }
    
    public boolean checkout(UUID cartId) {
        Cart cart = getOrCreateCart(cartId);
        cartRepository.delete(cart);
        return true;
    }

}
