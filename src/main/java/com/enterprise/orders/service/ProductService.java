package com.enterprise.orders.service;

import com.enterprise.orders.entity.Product;
import com.enterprise.orders.repository.ProductRepository;
import com.enterprise.orders.service.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product create(Product product) {

        if (product.getPrice() == null ||
                product.getPrice().signum() < 0) {

            throw new IllegalArgumentException(
                    "O preço não pode ser negativo.");
        }

        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public Product findById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produto não encontrado: " + id));
    }
}