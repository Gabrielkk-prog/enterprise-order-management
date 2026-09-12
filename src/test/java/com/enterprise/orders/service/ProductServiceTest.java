package com.enterprise.orders.service;

import com.enterprise.orders.entity.Product;
import com.enterprise.orders.service.exception.ResourceNotFoundException;
import com.enterprise.orders.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldCreateProductSuccessfully() {

        Product product = new Product(
                "Notebook Pro",
                "Notebook para desenvolvimento",
                new BigDecimal("4500.00"));

        when(productRepository.save(product))
                .thenReturn(product);

        Product result = productService.create(product);

        assertNotNull(result);
        assertEquals(
                new BigDecimal("4500.00"),
                result.getPrice());

        verify(productRepository).save(product);
    }

    @Test
    void shouldRejectNegativePrice() {

        Product product = new Product(
                "Produto inválido",
                "Teste",
                new BigDecimal("-100.00"));

        assertThrows(
                IllegalArgumentException.class,
                () -> productService.create(product));

        verify(productRepository, never())
                .save(any(Product.class));
    }

    @Test
    void shouldFindProductById() {

        Product product = new Product(
                "Notebook Pro",
                "Notebook para desenvolvimento",
                new BigDecimal("4500.00"));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        Product result = productService.findById(1L);

        assertNotNull(result);
        assertEquals("Notebook Pro", result.getName());
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {

        when(productRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> productService.findById(999L));
    }
}