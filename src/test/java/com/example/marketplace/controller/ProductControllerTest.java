package com.example.marketplace.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.marketplace.entity.Product;
import com.example.marketplace.exception.NotFoundException;
import com.example.marketplace.service.ProductService;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void getAllProducts_ShouldReturnProducts() throws Exception {
        // Given
        List<Product> products = Arrays.asList(
            new Product(UUID.randomUUID(), "Laptop", 1499.99, "A powerful laptop", 12),
            new Product(UUID.randomUUID(), "Mouse", 25.99, "Wireless mouse", 12)
        );
        when(productService.getAllProducts()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[0].price").value(1499.99))
                .andExpect(jsonPath("$[1].name").value("Mouse"))
                .andExpect(jsonPath("$[1].price").value(25.99));

        verify(productService).getAllProducts();
    }

    @Test
    void getAllProducts_ShouldReturnEmptyList_WhenNoProducts() throws Exception {
        // Given
        when(productService.getAllProducts()).thenReturn(Arrays.asList());

        // When & Then
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(productService).getAllProducts();
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() throws Exception {
        // Given
        UUID productId = UUID.randomUUID();
        Product product = new Product(productId, "Laptop", 1499.99, "A powerful laptop", 15);
        when(productService.findById(productId)).thenReturn(product);

        // When & Then
        mockMvc.perform(get("/api/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productId.toString()))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1499.99))
                .andExpect(jsonPath("$.stock").value(15));

        verify(productService).findById(productId);
    }

    @Test
    void getProductById_ShouldReturn404_WhenProductNotFound() throws Exception {
        // Given
        UUID productId = UUID.randomUUID();
        when(productService.findById(productId)).thenThrow(
            new NotFoundException("Product not found with ID: " + productId));

        // When & Then
        mockMvc.perform(get("/api/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Product not found with ID: " + productId));

        verify(productService).findById(productId);
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
                .andExpect(jsonPath("$.message").value("Invalid product ID format. Please provide a valid UUID."));

        verify(productService, never()).findById(any());
    }
}
