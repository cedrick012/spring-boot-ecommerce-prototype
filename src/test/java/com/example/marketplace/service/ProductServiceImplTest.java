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
import com.example.marketplace.mapper.ProductMapper;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        product1 = new Product(1L, "Laptop", 1499.99, "A powerful laptop", 12);
        product2 = new Product(2L, "Mouse", 25.99, "Wireless mouse", 2);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Given
        List<Product> expectedProducts = Arrays.asList(product1, product2);
        when(productMapper.findAll()).thenReturn(expectedProducts);

        // When
        List<Product> actualProducts = productService.getAllProducts();

        // Then
        assertEquals(2, actualProducts.size());
        assertEquals(expectedProducts, actualProducts);
        verify(productMapper, times(1)).findAll();
    }

    @Test
    void getAllProducts_ShouldReturnEmptyList_WhenNoProducts() {
        // Given
        when(productMapper.findAll()).thenReturn(Arrays.asList());

        // When
        List<Product> actualProducts = productService.getAllProducts();

        // Then
        assertTrue(actualProducts.isEmpty());
        verify(productMapper, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnProduct_WhenProductExists() {
        // Given
        Long productId = 1L;
        Product expectedProduct = new Product(productId, "Laptop", 1499.99, "A powerful laptop", 10);
        when(productMapper.findById(productId)).thenReturn(Optional.of(expectedProduct));

        // When
        Product actualProduct = productService.findById(productId);

        // Then
        assertNotNull(actualProduct);
        assertEquals(expectedProduct.getId(), actualProduct.getId());
        assertEquals(expectedProduct.getName(), actualProduct.getName());
        assertEquals(expectedProduct.getPrice(), actualProduct.getPrice());
        assertEquals(expectedProduct.getStock(), actualProduct.getStock());
        verify(productMapper, times(1)).findById(productId);
    }

    @Test
    void findById_ShouldThrowNotFoundException_WhenProductDoesNotExist() {
        // Given
        Long productId = 1L;
        when(productMapper.findById(productId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class, 
            () -> productService.findById(productId));
        
        assertEquals(productId + " の商品が見つかりません。", exception.getMessage());
        verify(productMapper, times(1)).findById(productId);
    }

    @Test
    void reduceStock_shouldUpdateStock_whenSufficientStockExists() {
        // Given
        Long productId = product1.getId();
        int initialStock = product1.getStock();
        int quantityToReduce = 5;
        when(productMapper.findById(productId)).thenReturn(Optional.of(product1));

        // When
        productService.reduceStock(productId, quantityToReduce);

        // Then
        verify(productMapper, times(1)).findById(productId);
        verify(productMapper, times(1)).update(product1);
        assertEquals(initialStock - quantityToReduce, product1.getStock());
    }

    @Test
    void reduceStock_shouldThrowException_whenInsufficientStock() {
        // Given
        Long productId = product2.getId();
        int quantityToReduce = 5; // More than available stock (2)
        when(productMapper.findById(productId)).thenReturn(Optional.of(product2));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            productService.reduceStock(productId, quantityToReduce));

        assertTrue(exception.getMessage().contains("在庫不足です。"));
        verify(productMapper, never()).save(any());
    }

    @Test
    void reduceStock_shouldThrowException_whenProductNotFound() {
        // Given
        Long nonExistentProductId = 1L;
        when(productMapper.findById(nonExistentProductId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () ->
            productService.reduceStock(nonExistentProductId, 1));
        verify(productMapper, never()).save(any());
    }

    @Test
    void reduceStock_shouldThrowException_whenQuantityIsZero() {
        // Given
        Long productId = product1.getId();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            productService.reduceStock(productId, 0));

        assertEquals("削減する数量は、0より大きい値である必要があります。", exception.getMessage());
        verify(productMapper, never()).save(any());
    }

    @Test
    void reduceStock_shouldThrowException_whenQuantityIsNegative() {
        // Given
        Long productId = product1.getId();

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            productService.reduceStock(productId, -1));

        assertEquals("削減する数量は、0より大きい値である必要があります。", exception.getMessage());
        verify(productMapper, never()).save(any());
    }
}
