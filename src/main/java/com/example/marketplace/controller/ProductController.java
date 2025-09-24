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
import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable @NotNull UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }
        return productService.findById(id);
    }
}
