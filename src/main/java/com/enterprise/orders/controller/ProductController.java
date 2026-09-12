package com.enterprise.orders.controller;

import com.enterprise.orders.dto.product.ProductRequest;
import com.enterprise.orders.dto.product.ProductResponse;
import com.enterprise.orders.entity.Product;
import com.enterprise.orders.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

        private final ProductService productService;

        public ProductController(ProductService productService) {
                this.productService = productService;
        }

        @PostMapping
        public ResponseEntity<ProductResponse> create(
                        @RequestBody ProductRequest request) {

                Product product = new Product(
                                request.name(),
                                request.description(),
                                request.price());

                Product savedProduct = productService.create(product);

                ProductResponse response = new ProductResponse(
                                savedProduct.getId(),
                                savedProduct.getName(),
                                savedProduct.getDescription(),
                                savedProduct.getPrice(),
                                savedProduct.isActive());

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(response);
        }

        @GetMapping("/{id}")
        public ResponseEntity<ProductResponse> findById(
                        @PathVariable Long id) {

                Product product = productService.findById(id);

                ProductResponse response = new ProductResponse(
                                product.getId(),
                                product.getName(),
                                product.getDescription(),
                                product.getPrice(),
                                product.isActive());

                return ResponseEntity.ok(response);
        }
}