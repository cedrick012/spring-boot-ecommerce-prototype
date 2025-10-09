package com.example.marketplace.mapper;

import com.example.marketplace.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductMapper {
    List<Product> findAll();
    Optional<Product> findById(Long id);
    void update(Product product);
    void save(Product product);
}