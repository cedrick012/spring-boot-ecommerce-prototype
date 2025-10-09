package com.example.marketplace.mapper;

import com.example.marketplace.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Optional;

@Mapper
public interface CartMapper {
    Optional<Cart> findById(Long id);
    List<Cart> findBySessionId(String sessionId);
    void save(Cart cart);
    void deleteById(Long id);
}