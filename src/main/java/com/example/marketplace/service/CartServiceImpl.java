package com.example.marketplace.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.dto.CheckoutResult;
import com.example.marketplace.entity.Cart;
import com.example.marketplace.entity.CartItem;
import com.example.marketplace.entity.Product;
import com.example.marketplace.exception.NotFoundException;
import com.example.marketplace.mapper.CartItemMapper;
import com.example.marketplace.mapper.CartMapper;
import com.example.marketplace.mapper.ProductMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final CartItemMapper cartItemMapper;
    private final ProductService productService;
    
    @Override
    @Transactional
    public Cart addProductToCart(Long cartId, Long productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("数量は0より大きい値である必要があります。");
        }
        
        Cart cart = cartMapper.findById(cartId)
            .orElseThrow(() -> new NotFoundException(cartId + " のカートが見つかりません。"));
        
        Product product = productMapper.findById(productId)
                .orElseThrow(() -> new NotFoundException(productId + " の商品が見つかりません。"));
        
        Optional<CartItem> existingCartItemOpt = cartItemMapper.findByCartIdAndProductId(cart.getId(), product.getId());

        int currentCartItemQuantity = existingCartItemOpt.map(CartItem::getQuantity).orElse(0);
        
        if (currentCartItemQuantity + quantity > product.getStock()) {
            throw new IllegalArgumentException("在庫不足です。在庫: " + 
                (product.getStock() - currentCartItemQuantity) + ", ご要望: " + quantity);
        }

        if (existingCartItemOpt.isPresent()) {
            CartItem item = existingCartItemOpt.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemMapper.update(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemMapper.save(newItem);
        }
        
        return getCart(cartId);
    }

    @Override
    public Cart getOrCreateCartBySession(String sessionId) {
        return cartMapper.findBySessionId(sessionId)
        	    .stream()
        	    .findFirst()
        	    .orElseGet(() -> createNewCart(sessionId));
    }
    
    private Cart createNewCart(String sessionId) {
        Cart newCart = new Cart();
        newCart.setSessionId(sessionId);
        newCart.setCreatedAt(LocalDateTime.now());
        newCart.setUpdatedAt(LocalDateTime.now());
        cartMapper.save(newCart);
        return newCart;
    }

    @Override
    public Cart getCart(Long cartId) {
        if (cartId == null) {
            throw new IllegalArgumentException("カートIDはNULLにできません。");
        }
        return cartMapper.findById(cartId)
            .orElseThrow(() -> new NotFoundException(cartId + " のカートが見つかりません。"));
    }
    
    @Override
    @Transactional
    public CheckoutResult checkout(Long cartId) {
        try {
            Cart cart = cartMapper.findById(cartId)
                .orElseThrow(() -> new NotFoundException(cartId + " のカートが見つかりません。"));
            
            if (cart.getItems().isEmpty()) {
                return CheckoutResult.failure("空のカートは精算できません。");
            }
            
            List<String> errors = new ArrayList<>();
            
            for (CartItem item : cart.getItems()) {
                Product product = item.getProduct();
                if (product.getStock() < item.getQuantity()) {
                    errors.add(String.format("%s の在庫が不足しています。在庫数: %d, 必要数: %d", 
                        product.getName(), product.getStock(), item.getQuantity()));
                }
            }
            
            if (!errors.isEmpty()) {
                return CheckoutResult.failure("在庫不足のため、精算に失敗しました。", errors);
            }
            
            for (CartItem item : cart.getItems()) {
                productService.reduceStock(item.getProduct().getId(), item.getQuantity());
            }
            
            cartMapper.deleteById(cart.getId());
            
            return CheckoutResult.success("精算が完了しました！ご注文が確定されました。");
            
        } catch (NotFoundException e) {
            return CheckoutResult.failure(e.getMessage());
        } catch (IllegalArgumentException e) {
            return CheckoutResult.failure("精算に失敗しました: " + e.getMessage());
        } catch (Exception e) {
            // Log the exception for debugging
            // logger.error("Unexpected error during checkout", e);
            return CheckoutResult.failure("精算中に予期せぬエラーが発生しました。");
        }
    }
}
