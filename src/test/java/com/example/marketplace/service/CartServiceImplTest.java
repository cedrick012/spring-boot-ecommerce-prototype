package com.example.marketplace.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
import com.example.marketplace.repository.CartRepository;
import com.example.marketplace.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

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
        
        product = new Product(productId, "Laptop", 1499.99, "A powerful laptop");
        
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
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // When
        Cart result = cartService.addProductToCart(cartId, productId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        CartItem addedItem = result.getItems().iterator().next();
        assertEquals(product, addedItem.getProduct());
        assertEquals(1, addedItem.getQuantity());
        
        verify(cartRepository).findById(cartId);
        verify(productRepository).findById(productId);
        verify(cartRepository).save(cart);
    }

    @Test
    void addProductToCart_ShouldIncrementQuantity_WhenProductAlreadyInCart() {
        // Given
        cart.getItems().add(cartItem);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // When
        Cart result = cartService.addProductToCart(cartId, productId);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getItems().size());
        CartItem updatedItem = result.getItems().iterator().next();
        assertEquals(2, updatedItem.getQuantity());
        
        verify(cartRepository).save(cart);
    }

    @Test
    void addProductToCart_ShouldThrowException_WhenCartNotFound() {
        // Given
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> cartService.addProductToCart(cartId, productId));
        
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
            () -> cartService.addProductToCart(cartId, productId));
        
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
    void getCart_ShouldCreateNewCart_WhenCartIdIsNull() {
        // Given
        Cart newCart = new Cart();
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

        // When
        Cart result = cartService.getCart(null);

        // Then
        assertNotNull(result);
        verify(cartRepository).save(any(Cart.class));
        verify(cartRepository, never()).findById(any());
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
