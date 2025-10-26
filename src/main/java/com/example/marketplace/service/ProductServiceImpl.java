package com.example.marketplace.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.marketplace.entity.Product;
import com.example.marketplace.exception.NotFoundException;
import com.example.marketplace.mapper.ProductMapper;

import lombok.RequiredArgsConstructor;

/**
 * 商品関連のビジネスロジックを実装するサービスクラスです。
 * 商品の取得、在庫管理などの操作を提供します。
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
	
	private final ProductMapper productMapper;

	/**
	 * 登録されている全商品を取得します。
	 *
	 * @return 商品リスト
	 */
	@Override
	public List<Product> getAllProducts() {
		return productMapper.findAll();
	}

	/**
	 * 指定したIDの商品を取得します。
	 *
	 * @param id 取得する商品のID
	 * @return 該当する商品情報
	 * @throws NotFoundException 商品が見つからない場合
	 */
	@Override
	public Product findById(Long id) {
		return productMapper.findById(id)
			.orElseThrow(() -> new NotFoundException(id + " の商品が見つかりません。"));
	}

	/**
	 * 商品の在庫を減算します。
	 * 指定数量が0以下または在庫不足の場合は例外を送出します。
	 *
	 * @param productId 対象商品のID
	 * @param quantity 減算する数量（1以上）
	 * @throws IllegalArgumentException 数量が不正または在庫不足の場合
	 * @throws NotFoundException 商品が見つからない場合
	 */
	@Override
	public void reduceStock(Long productId, int quantity) {
		if (quantity <= 0) {
			throw new IllegalArgumentException("削減する数量は、0より大きい値である必要があります。");
		}
		
		Product product = productMapper.findById(productId)
			.orElseThrow(() -> new NotFoundException(productId + " の商品が見つかりません。"));
		
		if (product.getStock() < quantity) {
			throw new IllegalArgumentException("在庫不足です。在庫数: " + product.getStock() + ", ご要望数: " + quantity);
		}
		
		product.setStock(product.getStock() - quantity);
		productMapper.update(product);
	}
}
