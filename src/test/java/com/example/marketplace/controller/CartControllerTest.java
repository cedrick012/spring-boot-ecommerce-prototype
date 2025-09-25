package com.example.marketplace.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.marketplace.dto.AddToCartRequest;
import com.example.marketplace.entity.Cart;
import com.example.marketplace.entity.CartItem;
import com.example.marketplace.entity.Product;
import com.example.marketplace.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;
    
    private static String sessionId;
    private static LocalDateTime createdAt;
    private static LocalDateTime updatedAt;
    
    @BeforeAll
    static void setUp() {
        sessionId = UUID.randomUUID().toString();
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @Test
    void getCart_ShouldReturnCart_WhenCartExists() throws Exception {
        // Given
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart(cartId, sessionId, createdAt, updatedAt, new HashSet<>());
        when(cartService.getCart(cartId)).thenReturn(cart);

        // When & Then
        mockMvc.perform(get("/api/carts/{id}", cartId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cartId.toString()));

        verify(cartService).getCart(cartId);
    }

    @Test
    void createCart_ShouldReturnNewCart() throws Exception {
        // Given
        Cart newCart = new Cart(UUID.randomUUID(), sessionId, createdAt, updatedAt, new HashSet<>());
        when(cartService.getCart(null)).thenReturn(newCart);

        // When & Then
        mockMvc.perform(post("/api/carts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newCart.getId().toString()));

        verify(cartService).getCart(null);
    }

    @Test
    void addProductToCart_ShouldAddProduct() throws Exception {
        // Given
        UUID cartId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        
        Product product = new Product(productId, "Laptop", 1499.99, "A laptop", 12);
        Cart cart = new Cart(cartId,sessionId, createdAt, updatedAt, new HashSet<>());
        CartItem cartItem = new CartItem(UUID.randomUUID(), product, 1, cart);
        cart.getItems().add(cartItem);
        
        
        int quantity = 2;
        when(cartService.addProductToCart(cartId, productId, quantity)).thenReturn(cart);
        
        AddToCartRequest addToCartRequestDto = new AddToCartRequest();
        addToCartRequestDto.setProductId(productId);
        addToCartRequestDto.setQuantity(quantity);

        String requestBody = objectMapper.writeValueAsString(
        		addToCartRequestDto
        );

        // When & Then
        mockMvc.perform(post("/api/carts/{id}/add-product", cartId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk());

        verify(cartService).addProductToCart(cartId, productId, quantity);
    }

    @Test
    void addProductToCart_ShouldReturnBadRequest_WhenProductIdIsNull() throws Exception {
        // Given
        UUID cartId = UUID.randomUUID();
        String requestBody = "{}";

        // When & Then
        mockMvc.perform(post("/api/carts/{id}/add-product", cartId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isBadRequest());

        verify(cartService, never()).addProductToCart(any(), any(), any());
    }

    @Test
    void checkout_ShouldReturnSuccessMessage() throws Exception {
        // Given
        UUID cartId = UUID.randomUUID();
        when(cartService.checkout(cartId)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/carts/{id}/checkout", cartId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Checkout successful! Your order has been placed."));

        verify(cartService).checkout(cartId);
    }
}
