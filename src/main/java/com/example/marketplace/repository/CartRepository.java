package com.example.marketplace.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.marketplace.entity.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, UUID> {
}
