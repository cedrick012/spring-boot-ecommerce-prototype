package com.example.marketplace.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.HashSet;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.marketplace.entity.Cart;
import com.example.marketplace.entity.CartItem;
import com.example.marketplace.entity.Product;
import com.example.marketplace.exception.NotFoundException;
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

    @Test
    void getCart_ShouldReturnCart_WhenCartExists() throws Exception {
        // Given
        UUID cartId = UUID.randomUUID();
        Cart cart = new Cart(cartId, new HashSet<>());
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
        Cart newCart = new Cart(UUID.randomUUID(), new HashSet<>());
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
        
        Product product = new Product(productId, "Laptop", 1499.99, "A laptop");
        Cart cart = new Cart(cartId, new HashSet<>());
        CartItem cartItem = new CartItem(UUID.randomUUID(), product, 1, cart);
        cart.getItems().add(cartItem);
        
        when(cartService.addProductToCart(cartId, productId)).thenReturn(cart);

        String requestBody = objectMapper.writeValueAsString(
            new com.example.marketplace.dto.AddToCartRequest() {{
                setProductId(productId);
            }}
        );

        // When & Then
        mockMvc.perform(post("/api/carts/{id}/add-product", cartId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cartId.toString()))
                .andExpect(jsonPath("$.items").isArray());

        verify(cartService).addProductToCart(cartId, productId);
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

        verify(cartService, never()).addProductToCart(any(), any());
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
