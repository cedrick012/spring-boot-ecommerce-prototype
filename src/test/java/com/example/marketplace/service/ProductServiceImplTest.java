package com.example.marketplace.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.marketplace.entity.Product;
import com.example.marketplace.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product(UUID.randomUUID(), "Laptop", 1499.99, "A powerful laptop");
        product2 = new Product(UUID.randomUUID(), "Mouse", 25.99, "Wireless mouse");
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Given
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        when(productRepository.findAll()).thenReturn(expectedProducts);

        // When
        List<Product> actualProducts = productService.getAllProducts();

        // Then
        assertEquals(2, actualProducts.size());
        assertEquals(expectedProducts, actualProducts);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getAllProducts_ShouldReturnEmptyList_WhenNoProducts() {
        // Given
        when(productRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<Product> actualProducts = productService.getAllProducts();

        // Then
        assertTrue(actualProducts.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnProduct_WhenProductExists() {
        // Given
        UUID productId = UUID.randomUUID();
        Product expectedProduct = new Product(productId, "Laptop", 1499.99, "A powerful laptop", 10);
        when(productRepository.findById(productId)).thenReturn(Optional.of(expectedProduct));

        // When
        Product actualProduct = productService.findById(productId);

        // Then
        assertNotNull(actualProduct);
        assertEquals(expectedProduct.getId(), actualProduct.getId());
        assertEquals(expectedProduct.getName(), actualProduct.getName());
        assertEquals(expectedProduct.getPrice(), actualProduct.getPrice());
        assertEquals(expectedProduct.getStock(), actualProduct.getStock());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenProductDoesNotExist() {
        // Given
        UUID productId = UUID.randomUUID();
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> productService.findById(productId));
        
        assertEquals("Product not found with ID: " + productId, exception.getMessage());
        verify(productRepository, times(1)).findById(productId);
    }
}
