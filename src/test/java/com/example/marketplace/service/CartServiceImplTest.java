package com.example.marketplace.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.marketplace.entity.Cart;
import com.example.marketplace.entity.CartItem;
import com.example.marketplace.entity.Product;
import com.example.marketplace.exception.NotFoundException;
import com.example.marketplace.repository.CartItemRepository;
import com.example.marketplace.repository.CartRepository;
import com.example.marketplace.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private Cart cart;
    private Product product;
    private CartItem cartItem;
    private UUID cartId;
    private UUID productId;

    @BeforeEach
    void setUp() {
        cartId = UUID.randomUUID();
        productId = UUID.randomUUID();
        
        product = new Product(productId, "Laptop", 1499.99, "A powerful laptop", 12);
        
        cart = new Cart();
        cart.setId(cartId);
        cart.setItems(new HashSet<>());
        
        cartItem = new CartItem(UUID.randomUUID(), product, 1, cart);
    }

    @Test
    void addProductToCart_ShouldAddNewProduct_WhenProductNotInCart() {
        // Given
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(any(), any()))
        .thenReturn(Optional.empty());

        // When
        Cart result = cartService.addProductToCart(cartId, productId, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        CartItem addedItem = result.getItems().iterator().next();
        assertEquals(product, addedItem.getProduct());
        assertEquals(1, addedItem.getQuantity());
        
        verify(cartRepository).findById(cartId);
        verify(productRepository).findById(productId);
        verify(cartItemRepository).save(addedItem);
    }

    @Test
    void addProductToCart_ShouldIncrementQuantity_WhenProductAlreadyInCart() {
        // Given
        cart.getItems().add(cartItem);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(any(), any()))
        .thenReturn(Optional.of(cartItem));

        // When
        Cart result = cartService.addProductToCart(cartId, productId, 1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        CartItem updatedItem = result.getItems().iterator().next();
        assertEquals(2, updatedItem.getQuantity());
        
        verify(cartItemRepository).save(cartItem);
    }

    @Test
    void addProductToCart_ShouldThrowException_WhenCartNotFound() {
        // Given
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> cartService.addProductToCart(cartId, productId, 12));
        
        assertEquals("Cart not found with ID: " + cartId, exception.getMessage());
        verify(cartRepository).findById(cartId);
        verify(productRepository, never()).findById(any());
        verify(cartRepository, never()).save(any());
    }

    @Test
    void addProductToCart_ShouldThrowException_WhenProductNotFound() {
        // Given
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> cartService.addProductToCart(cartId, productId, 12));
        
        assertEquals("Product not found with ID: " + productId, exception.getMessage());
        verify(cartRepository).findById(cartId);
        verify(productRepository).findById(productId);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void getCart_ShouldReturnCart_WhenCartExists() {
        // Given
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        // When
        Cart result = cartService.getCart(cartId);

        // Then
        assertNotNull(result);
        assertEquals(cartId, result.getId());
        verify(cartRepository).findById(cartId);
    }

    @Test
    void getCart_ShouldThrowException_WhenCartNotFound() {
        // Given
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> cartService.getCart(cartId));
        
        assertEquals("Cart not found with ID: " + cartId, exception.getMessage());
        verify(cartRepository).findById(cartId);
    }

    @Test
    void checkout_ShouldDeleteCart_WhenCartExists() {
        // Given
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        // When
        boolean result = cartService.checkout(cartId);

        // Then
        assertTrue(result);
        verify(cartRepository).findById(cartId);
        verify(cartRepository).delete(cart);
    }

    @Test
    void checkout_ShouldThrowException_WhenCartNotFound() {
        // Given
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> cartService.checkout(cartId));
        
        assertEquals("Cart not found with ID: " + cartId, exception.getMessage());
        verify(cartRepository).findById(cartId);
        verify(cartRepository, never()).delete(any());
    }
}
