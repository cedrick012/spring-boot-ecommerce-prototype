package com.example.marketplace.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.marketplace.dto.AddToCartRequest;
import com.example.marketplace.dto.CheckoutResult;
import com.example.marketplace.entity.Cart;
import com.example.marketplace.service.CartService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * カート関連のREST APIエンドポイントを提供するコントローラーです。
 * セッション方式のカート管理と商品追加、精算機能を提供します。
 */
@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {
	
    private final CartService cartService;

    /**
     * 指定IDのカート情報を取得します。
     * 
     * GET /api/carts/{id}
     *
     * @param id カートID
     * @return カート情報
     * @throws NotFoundException カートが見つからない場合
     */
    @GetMapping("/{id}")
    public Cart getCart(@PathVariable Long id) {
        return cartService.getCart(id);
    }
    
    /**
     * 新しいカートを作成します。
     * 
     * POST /api/carts
     *
     * @return 作成されたカート情報
     */
    @PostMapping
    public Cart createCart() {
    	return cartService.getCart(null);
    }
    
    /**
     * 指定カートに商品を追加します。
     * 
     * POST /api/carts/{id}/add-product
     *
     * @param id カートID
     * @param bodyDto 追加する商品情報（商品ID、数量）
     * @return 更新後のカート情報
     */
    @PostMapping("/{id}/add-product")
    public Cart addProductToCart(
    		@PathVariable Long id,
    		@Valid @RequestBody AddToCartRequest bodyDto) {
        return cartService.addProductToCart(id, bodyDto.getProductId(), bodyDto.getQuantity());
    }
    
    /**
     * セッションに紐づくカートへ商品を追加します。
     * 
     * POST /api/carts/session/add-product
     *
     * @param session HTTPセッション（カートの識別に使用）
     * @param bodyDto 追加する商品情報（商品ID、数量）
     * @return 更新後のカート情報
     */
    @PostMapping("/session/add-product")  
    public Cart addProductToCartBySession(
            HttpSession session,
            @Valid @RequestBody AddToCartRequest bodyDto) {
        String sessionId = session.getId();
        Cart cart = cartService.getOrCreateCartBySession(sessionId);
        return cartService.addProductToCart(cart.getId(), bodyDto.getProductId(), bodyDto.getQuantity());
    }
    
    /**
     * セッションに紐づくカート情報を取得します。
     * 
     * GET /api/carts/session
     *
     * @param session HTTPセッション
     * @return カート情報（存在しない場合は新規作成）
     */
    @GetMapping("/session")
    public Cart getCartBySession(HttpSession session) {
        String sessionId = session.getId();
        return cartService.getOrCreateCartBySession(sessionId);
    }
    
    /**
     * カートの精算処理を実行します。
     * 在庫検証後、在庫を減算してカートを削除します。
     * 
     * DELETE /api/carts/{id}/checkout
     *
     * @param id 精算対象のカートID
     * @return 精算結果（成功/失敗）
     */
    @DeleteMapping("/{id}/checkout")
    public ResponseEntity<?> checkout(@PathVariable Long id) {
        CheckoutResult result = cartService.checkout(id);
        
        if (result.isSuccess()) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }

}