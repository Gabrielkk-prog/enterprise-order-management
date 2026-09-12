package com.enterprise.orders.controller;

import com.enterprise.orders.dto.order.CreateOrderRequest;
import com.enterprise.orders.dto.order.OrderItemResponse;
import com.enterprise.orders.dto.order.OrderResponse;
import com.enterprise.orders.entity.Order;
import com.enterprise.orders.entity.OrderItem;
import com.enterprise.orders.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(
            @RequestBody @Valid CreateOrderRequest request) {

        Order order = orderService.createOrder(
                request.customerId(),
                request.items());

        OrderResponse response = toResponse(order);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    private OrderResponse toResponse(Order order) {

        var items = order.getItems()
                .stream()
                .map(this::toItemResponse)
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getId(),
                order.getCustomer().getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                items);
    }

    private OrderItemResponse toItemResponse(OrderItem item) {

        return new OrderItemResponse(
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal());
    }
}