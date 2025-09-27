package com.example.marketplace.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.marketplace.entity.Product;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void save_shouldPersistProduct() {
        // Given
        Product product = new Product(null, "Test Keyboard", 120.00, "A mechanical keyboard", 50);

        // When
        Product savedProduct = productRepository.save(product);

        // Then
        assertThat(savedProduct).isNotNull();
        assertThat(savedProduct.getId()).isNotNull();
        Product foundProduct = entityManager.find(Product.class, savedProduct.getId());
        assertThat(foundProduct.getName()).isEqualTo("Test Keyboard");
    }

    @Test
    void findById_shouldReturnProduct_whenProductExists() {
        // Given
        Product product = new Product(null, "Test Monitor", 300.00, "A 4K monitor", 20);
        UUID id = entityManager.persistAndGetId(product, UUID.class);
        entityManager.flush();

        // When
        Optional<Product> foundProduct = productRepository.findById(id);

        // Then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getId()).isEqualTo(id);
    }

    @Test
    void findById_shouldReturnEmpty_whenProductDoesNotExist() {
        // When
        Optional<Product> foundProduct = productRepository.findById(UUID.randomUUID());

        // Then
        assertThat(foundProduct).isNotPresent();
    }

    @Test
    void delete_shouldRemoveProduct() {
        // Given
        Product product = new Product(null, "To Be Deleted", 5.0, "Temp product", 5);
        UUID id = entityManager.persistAndGetId(product, UUID.class);
        entityManager.flush();

        // When
        productRepository.deleteById(id);
        entityManager.flush();

        // Then
        Product deletedProduct = entityManager.find(Product.class, id);
        assertThat(deletedProduct).isNull();
    }
}
