package com.example.marketplace.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.marketplace.entity.Product;
import com.example.marketplace.exception.NotFoundException;
import com.example.marketplace.service.CartService;
import com.example.marketplace.service.ProductService;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    // Mock CartService to satisfy context creation for other controllers
    @MockBean
    private CartService cartService;

    @Test
    void getAllProducts_shouldReturnListOfProducts() throws Exception {
        Product product1 = new Product(UUID.randomUUID(), "Laptop", 1500.00, "High-end laptop", 10);
        Product product2 = new Product(UUID.randomUUID(), "Mouse", 75.50, "Gaming mouse", 50);
        when(productService.getAllProducts()).thenReturn(List.of(product1, product2));

        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].name", is("Laptop")))
            .andExpect(jsonPath("$[1].name", is("Mouse")));

        verify(productService).getAllProducts();
    }

    @Test
    void getAllProducts_shouldReturnEmptyList_whenNoProductsExist() throws Exception {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(productService).getAllProducts();
    }

    @Test
    void getProductById_shouldReturnProduct_whenProductExists() throws Exception {
        UUID productId = UUID.randomUUID();
        Product product = new Product(productId, "Keyboard", 120.00, "Mechanical keyboard", 25);
        when(productService.findById(productId)).thenReturn(product);

        mockMvc.perform(get("/api/products/{id}", productId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(productId.toString())))
            .andExpect(jsonPath("$.name", is("Keyboard")));

        verify(productService).findById(productId);
    }

    @Test
    void getProductById_shouldReturnNotFound_whenProductDoesNotExist() throws Exception {
        UUID productId = UUID.randomUUID();
        when(productService.findById(productId)).thenThrow(new NotFoundException("商品が見つかりません。"));

        mockMvc.perform(get("/api/products/{id}", productId))
            .andExpect(status().isNotFound());

        verify(productService).findById(productId);
    }

    @Test
    void getProductById_shouldReturnBadRequest_whenIdIsInvalid() throws Exception {
    	
        mockMvc.perform(get("/api/products/{id}", "invalid-uuid"))
            .andExpect(status().isBadRequest());

        verify(productService, never()).findById(any());
    }

    @Test
    void getProductById_ShouldReturn400_WhenInvalidUUID() throws Exception {
        // Given
        String invalidId = "invalid-uuid";

        // When & Then
        mockMvc.perform(get("/api/products/{id}", invalidId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("商品IDの形式が無効です。有効なUUIDを指定してください。"));

        verify(productService, never()).findById(any());
    }
}
