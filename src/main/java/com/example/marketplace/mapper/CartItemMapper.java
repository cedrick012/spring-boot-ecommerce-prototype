package com.example.marketplace.mapper;

import com.example.marketplace.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Optional;

@Mapper
public interface CartItemMapper {
    Optional<CartItem> findByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);
    List<CartItem> findByCartId(Long cartId);
    void save(CartItem cartItem);
    void update(CartItem cartItem);
}