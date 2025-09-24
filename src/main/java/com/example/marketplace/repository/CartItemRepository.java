package com.example.marketplace.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.marketplace.entity.Cart;
import com.example.marketplace.entity.CartItem;
import com.example.marketplace.entity.Product;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
    
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    
    List<CartItem> findByCart(Cart cart);
    
    void deleteByCart(Cart cart);
}
