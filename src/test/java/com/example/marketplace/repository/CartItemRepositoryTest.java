package com.example.marketplace.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.marketplace.entity.Cart;
import com.example.marketplace.entity.CartItem;
import com.example.marketplace.entity.Product;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class CartItemRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CartItemRepository cartItemRepository;

    private Cart cart;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        cart = new Cart();
        cart.setSessionId("test-session");
        entityManager.persist(cart);

        product1 = new Product(null, "Product 1", 10.0, "Desc 1", 10);
        entityManager.persist(product1);

        product2 = new Product(null, "Product 2", 20.0, "Desc 2", 20);
        entityManager.persist(product2);

        entityManager.flush();
    }

    @Test
    void findByCartAndProduct_shouldReturnCartItem_whenExists() {
        // Given
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setProduct(product1);
        cartItem.setQuantity(2);
        entityManager.persistAndFlush(cartItem);

        // When
        Optional<CartItem> found = cartItemRepository.findByCartAndProduct(cart, product1);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getQuantity()).isEqualTo(2);
        assertThat(found.get().getProduct().getName()).isEqualTo("Product 1");
    }

    @Test
    void findByCartAndProduct_shouldReturnEmpty_whenNotExists() {
        // When
        Optional<CartItem> found = cartItemRepository.findByCartAndProduct(cart, product1);

        // Then
        assertThat(found).isNotPresent();
    }

    @Test
    void findByCart_shouldReturnAllItemsForThatCart() {
        // Given
        CartItem item1 = new CartItem();
        item1.setCart(cart);
        item1.setProduct(product1);
        item1.setQuantity(3);
        entityManager.persist(item1);

        CartItem item2 = new CartItem();
        item2.setCart(cart);
        item2.setProduct(product2);
        item2.setQuantity(5);
        entityManager.persist(item2);

        // When
        List<CartItem> items = cartItemRepository.findByCart(cart);

        // Then
        assertThat(items).hasSize(2);
        assertThat(items).extracting(CartItem::getQuantity).containsExactlyInAnyOrder(3, 5);
    }

    @Test
    void deleteByCart_shouldRemoveAllItemsForThatCart() {
        // Given
        CartItem item1 = new CartItem();
        item1.setCart(cart);
        item1.setProduct(product1);
        item1.setQuantity(1);
        entityManager.persistAndFlush(item1);

        // When
        cartItemRepository.deleteByCart(cart);
        entityManager.flush();
        entityManager.clear(); // Clear persistence context to ensure we hit the DB

        // Then
        List<CartItem> items = cartItemRepository.findByCart(cart);
        assertThat(items).isEmpty();
    }
}
