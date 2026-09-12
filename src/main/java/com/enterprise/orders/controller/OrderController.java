package com.enterprise.orders.controller;

import com.enterprise.orders.dto.order.CreateOrderRequest;
import com.enterprise.orders.entity.Order;
import com.enterprise.orders.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<Order> create(
            @RequestBody CreateOrderRequest request) {

        Order order = orderService.createOrder(
                request.customerId(),
                request.items());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(order);
    }
}
