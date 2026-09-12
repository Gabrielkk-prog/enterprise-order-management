package com.enterprise.orders.service;

import com.enterprise.orders.entity.*;
import com.enterprise.orders.repository.OrderRepository;
import com.enterprise.orders.repository.StockRepository;
import com.enterprise.orders.service.exception.BusinessException;
import com.enterprise.orders.dto.order.OrderItemRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

        private final OrderRepository orderRepository;
        private final StockRepository stockRepository;
        private final CustomerService customerService;
        private final ProductService productService;

        public OrderService(
                        OrderRepository orderRepository,
                        StockRepository stockRepository,
                        CustomerService customerService,
                        ProductService productService) {

                this.orderRepository = orderRepository;
                this.stockRepository = stockRepository;
                this.customerService = customerService;
                this.productService = productService;
        }

        @Transactional
        public Order createOrder(
                        Long customerId,
                        List<OrderItemRequest> items) {

                Customer customer = customerService.findById(customerId);

                Order order = new Order(customer);

                for (OrderItemRequest request : items) {

                        Product product = productService.findById(request.productId());

                        if (!product.isActive()) {
                                throw new BusinessException(
                                                "Produto inativo: "
                                                                + product.getName());
                        }

                        Stock stock = stockRepository
                                        .findByProductId(product.getId())
                                        .orElseThrow(() -> new BusinessException(
                                                        "Produto sem estoque: "
                                                                        + product.getName()));

                        if (request.quantity() == null ||
                                        request.quantity() <= 0) {

                                throw new BusinessException(
                                                "A quantidade deve ser maior que zero.");
                        }

                        if (stock.getQuantity() < request.quantity()) {
                                throw new BusinessException(
                                                "Estoque insuficiente para: "
                                                                + product.getName());
                        }

                        OrderItem item = new OrderItem(
                                        product,
                                        request.quantity(),
                                        product.getPrice());

                        order.addItem(item);

                        stock.setQuantity(
                                        stock.getQuantity()
                                                        - request.quantity());
                }

                return orderRepository.save(order);
        }
}