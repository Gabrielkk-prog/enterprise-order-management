package com.enterprise.orders.dto.order;

public record OrderItemRequest(
                Long productId,
                Integer quantity) {
}