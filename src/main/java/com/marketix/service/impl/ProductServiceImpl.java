package com.marketix.service.impl;

import com.marketix.dao.ProductRepository;
import com.marketix.entity.Product;
import com.marketix.exception.ProductNotFoundException;
import com.marketix.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow(() ->
                new ProductNotFoundException(String.format("Product with id %d not found", id)));
    }

    @Override
    public Product add(Product product) {
        var now = LocalDateTime.now();
        product.setCreated(now);
        product.setModified(now);
        return productRepository.save(product);
    }

    @Override
    public Product update(Product product) {
        var old = getProductById(product.getId());
        product.setCreated(old.getCreated());
        product.setModified(LocalDateTime.now());
        return productRepository.save(product);
    }

    @Override
    public Product deleteById(Long id) {
        var deleted = getProductById(id);
        productRepository.deleteById(id);
        return deleted;
    }
}
