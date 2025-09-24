package com.example.marketplace.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.marketplace.entity.Product;
import com.example.marketplace.repository.ProductRepository;

@SpringBootTest
@AutoConfigureTestMvc
@Transactional
@ActiveProfiles("test")
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExistsInDatabase() throws Exception {
        // Given
        Product product = new Product(null, "Test Laptop", 999.99, "Test description", 5);
        Product savedProduct = productRepository.save(product);

        // When & Then
        mockMvc.perform(get("/api/products/{id}", savedProduct.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedProduct.getId().toString()))
                .andExpect(jsonPath("$.name").value("Test Laptop"))
                .andExpect(jsonPath("$.price").value(999.99))
                .andExpected(jsonPath("$.stock").value(5));
    }

    @Test
    void getProductById_ShouldReturn404_WhenProductNotInDatabase() throws Exception {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When & Then
        mockMvc.perform(get("/api/products/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpected(jsonPath("$.status").value(404));
    }

    @Test
    void getAllProducts_ShouldIncludeStockInformation() throws Exception {
        // Given
        Product product1 = new Product(null, "Product 1", 100.0, "Description 1", 10);
        Product product2 = new Product(null, "Product 2", 200.0, "Description 2", 0);
        productRepository.save(product1);
        productRepository.save(product2);

        // When & Then
        mockMvc.perform(get("/api/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[?(@.name=='Product 1')].stock").value(10))
                .andExpect(jsonPath("$[?(@.name=='Product 2')].stock").value(0));
    }
}
