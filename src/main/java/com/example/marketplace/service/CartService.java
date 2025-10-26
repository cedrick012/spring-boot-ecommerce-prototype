package com.example.marketplace.service;

import com.example.marketplace.dto.CheckoutResult;
import com.example.marketplace.entity.Cart;

/**
 * カート関連のビジネスロジックを提供するサービスインターフェースです。
 */
public interface CartService {
	/**
	 * 指定したカートに商品を追加します。
	 *
	 * @param cartId カートID
	 * @param productId 商品ID
	 * @param quantity 追加数量
	 * @return 更新後のカート情報
	 */
	Cart addProductToCart(Long cartId, Long productId, int quantity);
	
	/**
	 * 指定したIDのカートを取得します。
	 *
	 * @param cartid カートID
	 * @return カート情報
	 * @throws NotFoundException カートが見つからない場合
	 */
	Cart getCart(Long cartid);
	
	/**
	 * セッションIDに紐づくカートを取得または新規作成します。
	 *
	 * @param sessionId セッションID
	 * @return カート情報（既存または新規作成）
	 */
	Cart getOrCreateCartBySession(String sessionId);
	
	/**
	 * カートの精算処理を実行します。
	 *
	 * @param cartId 精算対象のカートID
	 * @return 精算結果
	 */
	CheckoutResult checkout(Long cartId);
}
