package com.example.marketplace.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.dto.CheckoutResult;
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
    private final ProductService productService;
    
    @Override
    public Cart addProductToCart(UUID cartId, UUID productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("数量は0より大きい値である必要があります。");
        }
        
        Cart cart = cartRepository.findById(cartId)
            .orElseThrow(() -> new NotFoundException(cartId + " のカートが見つかりません。"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(productId + " の商品が見つかりません。"));
        
        Optional<CartItem> existingCartItem = cartItemRepository.findByCartAndProduct(cart, product);

        // Check if there's enough stock
        int currentCartItemQuantity = existingCartItem.map(CartItem::getQuantity).orElse(0);
        
        if (currentCartItemQuantity + quantity > product.getStock()) {
            throw new IllegalArgumentException("在庫不足です。在庫: " + 
                (product.getStock() - currentCartItemQuantity) + ", ご要望: " + quantity);
        }

        if (existingCartItem.isPresent()) {
            // Update existing item quantity
            CartItem item = existingCartItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
            
            existingCartItem = Optional.of(item);
        } else {
            // Create new cart item
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
            
            existingCartItem = Optional.of(newItem);
        }
        
        Set<CartItem> existingItems = cart.getItems();
        existingItems.add(existingCartItem.get());
        cart.setItems(existingItems);
        
        return cart;
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
            throw new IllegalArgumentException("カートIDはNULLにできません。");
        }
        return cartRepository.findById(cartId)
            .orElseThrow(() -> new NotFoundException(cartId + " のカートが見つかりません。"));
    }
    
    @Override
    @Transactional
    public CheckoutResult checkout(UUID cartId) {
        try {
            Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new NotFoundException(cartId + " のカートが見つかりません。"));
            
            if (cart.getItems().isEmpty()) {
                return CheckoutResult.failure("空のカートは精算できません。");
            }
            
            List<String> errors = new ArrayList<>();
            
            // First, validate all items have sufficient stock
            for (CartItem item : cart.getItems()) {
                Product product = item.getProduct();
                if (product.getStock() < item.getQuantity()) {
                    errors.add(String.format("%s の在庫が不足しています。在庫数: %d, 必要数: %d", 
                        product.getName(), product.getStock(), item.getQuantity()));
                }
            }
            
            // If any stock validation failed, return error
            if (!errors.isEmpty()) {
                return CheckoutResult.failure("在庫不足のため、精算に失敗しました。", errors);
            }
            
            // Reduce stock for all items
            for (CartItem item : cart.getItems()) {
                productService.reduceStock(item.getProduct().getId(), item.getQuantity());
            }
            
            // Delete the cart after successful stock reduction
            cartRepository.delete(cart);
            
            return CheckoutResult.success("精算が完了しました！ご注文が確定されました。");
            
        } catch (NotFoundException e) {
            return CheckoutResult.failure(e.getMessage());
        } catch (IllegalArgumentException e) {
            return CheckoutResult.failure("精算に失敗しました: " + e.getMessage());
        } catch (Exception e) {
            return CheckoutResult.failure("精算中に予期せぬエラーが発生しました。");
        }
    }
}
