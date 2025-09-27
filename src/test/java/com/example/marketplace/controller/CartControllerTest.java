package com.example.marketplace.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.marketplace.dto.AddToCartRequest;
import com.example.marketplace.dto.CheckoutResult;
import com.example.marketplace.entity.Cart;
import com.example.marketplace.exception.NotFoundException;
import com.example.marketplace.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @Autowired
    private ObjectMapper objectMapper;

    private UUID cartId;
    private UUID productId;
    private Cart cart;
    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        cartId = UUID.randomUUID();
        productId = UUID.randomUUID();
        cart = new Cart(cartId, "test-session-id", null, null, new HashSet<>());
        session = new MockHttpSession(null, "test-session-id");
    }

    private String createAddToCartRequestBody(UUID productId, int quantity) throws Exception {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(productId);
        request.setQuantity(quantity);
        return objectMapper.writeValueAsString(request);
    }

    @Test
    void getCart_shouldReturnCart_whenCartExists() throws Exception {
        when(cartService.getCart(cartId)).thenReturn(cart);

        mockMvc.perform(get("/api/carts/{id}", cartId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(cartId.toString()));

        verify(cartService).getCart(cartId);
    }

    @Test
    void getCart_shouldReturnNotFound_whenCartDoesNotExist() throws Exception {
        when(cartService.getCart(cartId)).thenThrow(new NotFoundException("Cart not found"));

        mockMvc.perform(get("/api/carts/{id}", cartId))
            .andExpect(status().isNotFound());

        verify(cartService).getCart(cartId);
    }

    @Test
    void createCart_shouldReturnNewCart() throws Exception {
        when(cartService.getCart(null)).thenReturn(cart);

        mockMvc.perform(post("/api/carts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(cartId.toString()));

        verify(cartService).getCart(null);
    }

    @Test
    void addProductToCart_shouldReturnUpdatedCart_whenRequestIsValid() throws Exception {
        when(cartService.addProductToCart(cartId, productId, 2)).thenReturn(cart);

        mockMvc.perform(post("/api/carts/{id}/add-product", cartId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddToCartRequestBody(productId, 2)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(cartId.toString()));

        verify(cartService).addProductToCart(cartId, productId, 2);
    }

    @Test
    void addProductToCart_shouldReturnBadRequest_whenBodyIsInvalid() throws Exception {
        mockMvc.perform(post("/api/carts/{id}/add-product", cartId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"quantity\": 0}")) // Invalid quantity and missing productId
            .andExpect(status().isBadRequest());

        verify(cartService, never()).addProductToCart(any(), any(), anyInt());
    }

    @Test
    void addProductToCartBySession_shouldReturnUpdatedCart() throws Exception {
        when(cartService.getOrCreateCartBySession(session.getId())).thenReturn(cart);
        when(cartService.addProductToCart(cart.getId(), productId, 1)).thenReturn(cart);

        mockMvc.perform(post("/api/carts/session/add-product")
                .session(session)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createAddToCartRequestBody(productId, 1)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(cartId.toString()));

        verify(cartService).getOrCreateCartBySession(session.getId());
        verify(cartService).addProductToCart(cart.getId(), productId, 1);
    }

    @Test
    void getCartBySession_shouldReturnCart() throws Exception {
        when(cartService.getOrCreateCartBySession(session.getId())).thenReturn(cart);

        mockMvc.perform(get("/api/carts/session").session(session))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(cartId.toString()));

        verify(cartService).getOrCreateCartBySession(session.getId());
    }

    @Test
    void checkout_shouldReturnSuccessMessage_whenCheckoutSucceeds() throws Exception {
    	String responseMessage = "Checkout successful!";
        when(cartService.checkout(cartId)).thenReturn(CheckoutResult.success(responseMessage));

        mockMvc.perform(delete("/api/carts/{id}/checkout", cartId))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString(responseMessage)));

        verify(cartService).checkout(cartId);
    }

    @Test
    void checkout_shouldReturnBadRequest_whenCheckoutFails() throws Exception {
    	String responseMessage = "Insufficient stock";
        when(cartService.checkout(cartId)).thenReturn(CheckoutResult.failure(responseMessage));

        mockMvc.perform(delete("/api/carts/{id}/checkout", cartId))
            .andExpect(status().is4xxClientError())
            .andExpect(content().string(containsString(responseMessage)));

        verify(cartService).checkout(cartId);
    }

    @Test
    void checkout_shouldReturnNotFound_whenCartDoesNotExist() throws Exception {
        when(cartService.checkout(cartId)).thenThrow(new NotFoundException("Cart not found"));

        mockMvc.perform(delete("/api/carts/{id}/checkout", cartId))
            .andExpect(status().isNotFound());

        verify(cartService).checkout(cartId);
    }
}
