package com.enterprise.orders.config;

import com.enterprise.orders.entity.Customer;
import com.enterprise.orders.entity.Product;
import com.enterprise.orders.repository.CustomerRepository;
import com.enterprise.orders.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

        @Bean
        CommandLineRunner initDatabase(
                        CustomerRepository customerRepository,
                        ProductRepository productRepository) {

                return args -> {

                        Customer customer = customerRepository
                                        .findByEmail("gabriel@email.com")
                                        .orElseGet(() -> customerRepository.save(new Customer(
                                                        "Gabriel Silva",
                                                        "gabriel@email.com",
                                                        "12345678900")));

                        Product product = productRepository
                                        .findByNameContainingIgnoreCase("Notebook Pro")
                                        .stream()
                                        .findFirst()
                                        .orElseGet(() -> productRepository.save(new Product(
                                                        "Notebook Pro",
                                                        "Notebook para desenvolvimento",
                                                        new BigDecimal("4500.00"))));

                        System.out.println("Cliente salvo: "
                                        + customer.getId());

                        System.out.println("Produto salvo: "
                                        + product.getId());
                };
        }
}