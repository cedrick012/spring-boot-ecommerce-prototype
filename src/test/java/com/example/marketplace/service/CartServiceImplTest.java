package com.example.marketplace.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.marketplace.dto.CheckoutResult;
import com.example.marketplace.entity.Cart;
import com.example.marketplace.entity.CartItem;
import com.example.marketplace.entity.Product;
import com.example.marketplace.exception.NotFoundException;
import com.example.marketplace.repository.CartItemRepository;
import com.example.marketplace.repository.CartRepository;
import com.example.marketplace.repository.ProductRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartServiceImpl cartService;

    private UUID cartId;
    private UUID productId;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        cartId = UUID.randomUUID();
        productId = UUID.randomUUID();
        cart = new Cart();
        cart.setId(cartId);
        cart.setSessionId("test-session");
        cart.setItems(new HashSet<>());
        product = new Product(productId, "Test Product", 10.0, "Description", 20);
    }

    @Test
    void addProductToCart_shouldAddNewItem_whenCartIsEmpty() {
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        cartService.addProductToCart(cartId, productId, 5);

        verify(cartItemRepository).save(argThat(item ->
            item.getCart().equals(cart) &&
            item.getProduct().equals(product) &&
            item.getQuantity() == 5
        ));
    }

    @Test
    void addProductToCart_shouldUpdateQuantity_whenItemExists() {
        CartItem existingItem = new CartItem();
        existingItem.setId(UUID.randomUUID());
        existingItem.setCart(cart);
        existingItem.setProduct(product);
        existingItem.setQuantity(2);

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(existingItem));
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        cartService.addProductToCart(cartId, productId, 3);

        assertEquals(5, existingItem.getQuantity());
        verify(cartItemRepository).save(existingItem);
    }

    @Test
    void addProductToCart_shouldThrowException_whenQuantityIsZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            cartService.addProductToCart(cartId, productId, 0));
        assertEquals("Quantity must be a value greater than 0", exception.getMessage());
    }

    @Test
    void addProductToCart_shouldThrowException_whenCartNotFound() {
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
            cartService.addProductToCart(cartId, productId, 1));
    }

    @Test
    void addProductToCart_shouldThrowException_whenProductNotFound() {
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
            cartService.addProductToCart(cartId, productId, 1));
    }

    @Test
    void addProductToCart_shouldThrowException_whenInsufficientStock() {
        product.setStock(5);
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            cartService.addProductToCart(cartId, productId, 10));
        assertTrue(exception.getMessage().contains("Insufficient stock"));
    }

    // Tests for getOrCreateCartBySession
    @Test
    void getOrCreateCartBySession_shouldReturnExistingCart() {
        when(cartRepository.findBySessionId("test-session")).thenReturn(List.of(cart));
        Cart result = cartService.getOrCreateCartBySession("test-session");
        assertEquals(cart, result);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void getOrCreateCartBySession_shouldCreateNewCart() {
        when(cartRepository.findBySessionId("new-session")).thenReturn(List.of());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart savedCart = invocation.getArgument(0);
            savedCart.setId(UUID.randomUUID()); // Simulate DB generating an ID
            return savedCart;
        });

        Cart result = cartService.getOrCreateCartBySession("new-session");

        assertNotNull(result);
        assertEquals("new-session", result.getSessionId());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    // Tests for getCart
    @Test
    void getCart_shouldReturnCart_whenIdExists() {
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        Cart result = cartService.getCart(cartId);
        assertEquals(cart, result);
    }

    @Test
    void getCart_shouldThrowException_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> cartService.getCart(null));
    }

    @Test
    void getCart_shouldThrowException_whenIdNotFound() {
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cartService.getCart(cartId));
    }

    // Tests for checkout
    @Test
    void checkout_shouldSucceed_whenCartIsValid() {
        CartItem item = new CartItem();
        item.setId(UUID.randomUUID());
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(2);
        cart.setItems(Set.of(item));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        doNothing().when(productService).reduceStock(productId, 2);

        CheckoutResult result = cartService.checkout(cartId);

        assertTrue(result.isSuccess());
        verify(productService, times(1)).reduceStock(productId, 2);
        verify(cartRepository, times(1)).delete(cart);
    }

    @Test
    void checkout_shouldFail_whenCartIsEmpty() {
        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        CheckoutResult result = cartService.checkout(cartId);
        assertFalse(result.isSuccess());
        assertEquals("Cannot checkout empty cart", result.getMessage());
    }

    @Test
    void checkout_shouldFail_whenCartNotFound() {
        when(cartRepository.findById(cartId)).thenReturn(Optional.empty());
        CheckoutResult result = cartService.checkout(cartId);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Cart not found"));
    }

    @Test
    void checkout_shouldFail_whenInsufficientStock() {
        product.setStock(1);
        CartItem item = new CartItem();
        item.setId(UUID.randomUUID());
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(2);
        cart.setItems(Set.of(item));

        when(cartRepository.findById(cartId)).thenReturn(Optional.of(cart));

        CheckoutResult result = cartService.checkout(cartId);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("insufficient stock"));
        assertEquals(1, result.getErrors().size());
        verify(productService, never()).reduceStock(any(), any(Integer.class));
        verify(cartRepository, never()).delete(any());
    }
}
