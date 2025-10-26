package com.example.marketplace.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.marketplace.entity.Product;
import com.example.marketplace.service.ProductService;

import lombok.RequiredArgsConstructor;

import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import java.util.List;

/**
 * 商品関連のREST APIエンドポイントを提供するコントローラーです。
 * 商品の一覧取得と詳細情報取得機能を提供します。
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    /**
     * 商品一覧を取得します。
     * 
     * GET /api/products
     *
     * @return 登録されている全商品のリスト
     */
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * 指定IDの商品詳細情報を取得します。
     * 
     * GET /api/products/{id}
     *
     * @param id 取得する商品のID
     * @return 商品詳細情報
     * @throws NotFoundException 商品が見つからない場合
     */
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable @NotNull Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        return productService.findById(id);
    }
}
