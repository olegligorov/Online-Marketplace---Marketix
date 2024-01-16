package com.marketix.service;

import com.marketix.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProducts();

    Product getProductById(Long id);

    Product add(Product product);

    Product update(Product product);

    Product deleteById(Long id);
}
