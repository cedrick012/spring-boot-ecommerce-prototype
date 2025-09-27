package com.example.marketplace.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.marketplace.entity.Product;
import com.example.marketplace.exception.NotFoundException;
import com.example.marketplace.repository.ProductRepository;
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
        product1 = new Product(UUID.randomUUID(), "Laptop", 1499.99, "A powerful laptop", 12);
        product2 = new Product(UUID.randomUUID(), "Mouse", 25.99, "Wireless mouse", 2);
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

    @Test
    void reduceStock_shouldUpdateStock_whenSufficientStockExists() {
        // Given
        UUID productId = product1.getId();
        int initialStock = product1.getStock();
        int quantityToReduce = 5;
        when(productRepository.findById(productId)).thenReturn(Optional.of(product1));

        // When
        productService.reduceStock(productId, quantityToReduce);

        // Then
        verify(productRepository, times(1)).findById(productId);
        verify(productRepository, times(1)).save(product1);
        assertEquals(initialStock - quantityToReduce, product1.getStock());
    }

    @Test
    void reduceStock_shouldThrowException_whenInsufficientStock() {
        // Given
        UUID productId = product2.getId();
        int quantityToReduce = 5; // More than available stock (2)
        when(productRepository.findById(productId)).thenReturn(Optional.of(product2));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            productService.reduceStock(productId, quantityToReduce));

        assertTrue(exception.getMessage().contains("Insufficient stock"));
        verify(productRepository, never()).save(any());
    }

    @Test
    void reduceStock_shouldThrowException_whenProductNotFound() {
        // Given
        UUID nonExistentProductId = UUID.randomUUID();
        when(productRepository.findById(nonExistentProductId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () ->
            productService.reduceStock(nonExistentProductId, 1));
        verify(productRepository, never()).save(any());
    }

    @Test
    void reduceStock_shouldThrowException_whenQuantityIsZero() {
        // Given
        UUID productId = product1.getId();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            productService.reduceStock(productId, 0));

        assertEquals("Quantity to reduce must be greater than 0", exception.getMessage());
        verify(productRepository, never()).save(any());
    }

    @Test
    void reduceStock_shouldThrowException_whenQuantityIsNegative() {
        // Given
        UUID productId = product1.getId();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            productService.reduceStock(productId, -1));

        assertEquals("Quantity to reduce must be greater than 0", exception.getMessage());
        verify(productRepository, never()).save(any());
    }
}
