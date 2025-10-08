package com.example.marketplace.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.marketplace.dto.CheckoutResult;
import com.example.marketplace.entity.Cart;
import com.example.marketplace.entity.CartItem;
import com.example.marketplace.entity.Product;
import com.example.marketplace.exception.NotFoundException;
import com.example.marketplace.mapper.CartItemMapper;
import com.example.marketplace.mapper.CartMapper;
import com.example.marketplace.mapper.ProductMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartMapper cartMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CartItemMapper cartItemMapper;

    @Mock
    private ProductService productService;

    @InjectMocks
    private CartServiceImpl cartService;

    private Long cartId;
    private Long productId;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        cartId = 1L;
        productId = 1L;
        cart = new Cart();
        cart.setId(cartId);
        cart.setSessionId("test-session");
        cart.setItems(new HashSet<>());
        product = new Product(productId, "Test Product", 10.0, "Description", 20);
    }

    @Test
    void addProductToCart_shouldAddNewItem_whenCartIsEmpty() {
        when(cartMapper.findById(cartId)).thenReturn(Optional.of(cart));
        when(productMapper.findById(productId)).thenReturn(Optional.of(product));
        when(cartItemMapper.findByCartIdAndProductId(cart.getId(), product.getId())).thenReturn(Optional.empty());
        when(cartMapper.findById(cartId)).thenReturn(Optional.of(cart));

        cartService.addProductToCart(cartId, productId, 5);

        verify(cartItemMapper).save(argThat(item ->
            item.getCart().equals(cart) &&
            item.getProduct().equals(product) &&
            item.getQuantity() == 5
        ));
    }

    @Test
    void addProductToCart_shouldUpdateQuantity_whenItemExists() {
        CartItem existingItem = new CartItem();
        existingItem.setId(1L);
        existingItem.setCart(cart);
        existingItem.setProduct(product);
        existingItem.setQuantity(2);

        when(cartMapper.findById(cartId)).thenReturn(Optional.of(cart));
        when(productMapper.findById(productId)).thenReturn(Optional.of(product));
        when(cartItemMapper.findByCartIdAndProductId(cart.getId(), product.getId())).thenReturn(Optional.of(existingItem));
        when(cartMapper.findById(cartId)).thenReturn(Optional.of(cart));

        cartService.addProductToCart(cartId, productId, 3);

        verify(cartItemMapper).update(argThat(item ->
            item.getId().equals(existingItem.getId()) &&
            item.getQuantity() == 5
        ));
    }

    @Test
    void addProductToCart_shouldThrowException_whenQuantityIsNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            cartService.addProductToCart(cartId, productId, -1);
        });
    }

    @Test
    void addProductToCart_shouldThrowException_whenCartNotFound() {
        when(cartMapper.findById(cartId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> {
            cartService.addProductToCart(cartId, productId, 1);
        });
    }

    @Test
    void addProductToCart_shouldThrowException_whenProductNotFound() {
        when(cartMapper.findById(cartId)).thenReturn(Optional.of(cart));
        when(productMapper.findById(productId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> {
            cartService.addProductToCart(cartId, productId, 1);
        });
    }

    @Test
    void addProductToCart_shouldThrowException_whenStockIsInsufficient() {
        product.setStock(5);
        when(cartMapper.findById(cartId)).thenReturn(Optional.of(cart));
        when(productMapper.findById(productId)).thenReturn(Optional.of(product));
        when(cartItemMapper.findByCartIdAndProductId(cart.getId(), product.getId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            cartService.addProductToCart(cartId, productId, 10);
        });
    }

    @Test
    void getOrCreateCartBySession_shouldReturnExistingCart() {
        when(cartMapper.findBySessionId("test-session")).thenReturn(List.of(cart));
        Cart result = cartService.getOrCreateCartBySession("test-session");
        assertEquals(cart, result);
        verify(cartMapper, never()).save(any(Cart.class));
    }

    @Test
    void getOrCreateCartBySession_shouldCreateNewCart() {
        when(cartMapper.findBySessionId("new-session")).thenReturn(List.of());
        
        Cart result = cartService.getOrCreateCartBySession("new-session");

        assertNotNull(result);
        assertEquals("new-session", result.getSessionId());
        verify(cartMapper).save(any(Cart.class));
    }

    @Test
    void getCart_shouldReturnCart_whenExists() {
        when(cartMapper.findById(cartId)).thenReturn(Optional.of(cart));
        Cart result = cartService.getCart(cartId);
        assertEquals(cart, result);
    }

    @Test
    void getCart_shouldThrowException_whenNotExists() {
        when(cartMapper.findById(cartId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> {
            cartService.getCart(cartId);
        });
    }

    @Test
    void checkout_shouldSucceed_whenStockIsSufficient() {
        CartItem item1 = new CartItem(1L, product, 2, cart);
        Set<CartItem> items = new HashSet<>();
        items.add(item1);
        cart.setItems(items);

        when(cartMapper.findById(cartId)).thenReturn(Optional.of(cart));
        doNothing().when(productService).reduceStock(product.getId(), 2);
        doNothing().when(cartMapper).deleteById(cartId);

        CheckoutResult result = cartService.checkout(cartId);

        assertTrue(result.isSuccess());
        assertEquals("精算が完了しました！ご注文が確定されました。", result.getMessage());
        verify(productService).reduceStock(product.getId(), 2);
        verify(cartMapper).deleteById(cartId);
    }

    @Test
    void checkout_shouldFail_whenStockIsInsufficient() {
        CartItem item1 = new CartItem(1L, product, 25, cart);
        Set<CartItem> items = new HashSet<>();
        items.add(item1);
        cart.setItems(items);

        when(cartMapper.findById(cartId)).thenReturn(Optional.of(cart));
        
        CheckoutResult result = cartService.checkout(cartId);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("在庫不足のため、精算に失敗しました。"));
        verify(productService, never()).reduceStock(any(Long.class), any(Integer.class));
        verify(cartMapper, never()).deleteById(any(Long.class));
    }

    @Test
    void checkout_shouldThrowException_whenCartNotFound() {
    	CheckoutResult result = cartService.checkout(10000000000000L);
    	
    	assertTrue(!result.isSuccess());
    }


    @Test
    void addProductToCart_shouldThrowException_whenQuantityIsZero() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            cartService.addProductToCart(cartId, productId, 0));
        assertEquals("数量は0より大きい値である必要があります。", exception.getMessage());
    }


    @Test
    void addProductToCart_shouldThrowException_whenInsufficientStock() {
        product.setStock(5);
        when(cartMapper.findById(cartId)).thenReturn(Optional.of(cart));
        when(productMapper.findById(productId)).thenReturn(Optional.of(product));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
            cartService.addProductToCart(cartId, productId, 10));
        assertTrue(exception.getMessage().contains("在庫不足です。"));
    }

    // Tests for getOrCreateCartBySession

    // Tests for getCart
    @Test
    void getCart_shouldReturnCart_whenIdExists() {
        when(cartMapper.findById(cartId)).thenReturn(Optional.of(cart));
        Cart result = cartService.getCart(cartId);
        assertEquals(cart, result);
    }

    @Test
    void getCart_shouldThrowException_whenIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> cartService.getCart(null));
    }

    @Test
    void getCart_shouldThrowException_whenIdNotFound() {
        when(cartMapper.findById(cartId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> cartService.getCart(cartId));
    }

    // Tests for checkout
    @Test
    void checkout_shouldSucceed_whenCartIsValid() {
        CartItem item = new CartItem();
        item.setId(1L);
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(2);
        cart.setItems(Set.of(item));

        when(cartMapper.findById(cartId)).thenReturn(Optional.of(cart));
        doNothing().when(productService).reduceStock(productId, 2);

        CheckoutResult result = cartService.checkout(cartId);

        assertTrue(result.isSuccess());
        verify(productService, times(1)).reduceStock(productId, 2);
        verify(cartMapper, times(1)).deleteById(cartId);
    }

    @Test
    void checkout_shouldFail_whenCartIsEmpty() {
        when(cartMapper.findById(cartId)).thenReturn(Optional.of(cart));
        CheckoutResult result = cartService.checkout(cartId);
        assertFalse(result.isSuccess());
        assertEquals("空のカートは精算できません。", result.getMessage());
    }

    @Test
    void checkout_shouldFail_whenCartNotFound() {
        when(cartMapper.findById(cartId)).thenReturn(Optional.empty());
        CheckoutResult result = cartService.checkout(cartId);
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("カートが見つかりません。"));
    }

    @Test
    void checkout_shouldFail_whenInsufficientStock() {
        product.setStock(1);
        CartItem item = new CartItem();
        item.setId(1L);
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(2);
        cart.setItems(Set.of(item));

        when(cartMapper.findById(cartId)).thenReturn(Optional.of(cart));

        CheckoutResult result = cartService.checkout(cartId);

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("在庫不足のため、精算に失敗しました。"));
        assertEquals(1, result.getErrors().size());
        verify(productService, never()).reduceStock(any(), any(Integer.class));
        verify(cartMapper, never()).deleteById(cartId);
    }
}
