package com.example.marketplace.service;

import java.util.List;

import com.example.marketplace.entity.Product;

/**
 * 商品関連のビジネスロジックを提供するサービスインターフェースです。
 */
public interface ProductService {
	/**
	 * 登録されている全商品を取得します。
	 *
	 * @return 商品リスト
	 */
	List<Product> getAllProducts();
	
	/**
	 * 指定したIDの商品を取得します。
	 *
	 * @param id 取得する商品のID
	 * @return 該当する商品情報
	 * @throws NotFoundException 商品が見つからない場合
	 */
	Product findById(Long id);
	
	/**
	 * 商品の在庫を減算します。
	 *
	 * @param productId 対象商品のID
	 * @param quantity 減算する数量
	 * @throws IllegalArgumentException 数量が不正または在庫不足の場合
	 * @throws NotFoundException 商品が見つからない場合
	 */
	void reduceStock(Long productId, int quantity);
}
