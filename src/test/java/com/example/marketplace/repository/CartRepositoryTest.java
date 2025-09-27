package com.example.marketplace.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.marketplace.entity.Cart;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class CartRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartRepository cartRepository;

    @Test
    void findBySessionId_shouldReturnCart_whenSessionIdExists() {
        // Given
        Cart cart = new Cart();
        cart.setSessionId("test-session-123");
        entityManager.persistAndFlush(cart);

        // When
        List<Cart> foundCarts = cartRepository.findBySessionId("test-session-123");

        // Then
        assertThat(foundCarts).hasSize(1);
        assertThat(foundCarts.get(0).getSessionId()).isEqualTo("test-session-123");
    }

    @Test
    void findBySessionId_shouldReturnEmptyList_whenSessionIdDoesNotExist() {
        // When
        List<Cart> foundCarts = cartRepository.findBySessionId("non-existent-session");

        // Then
        assertThat(foundCarts).isEmpty();
    }
}
