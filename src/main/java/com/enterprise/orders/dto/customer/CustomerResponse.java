package com.enterprise.orders.dto.customer;

public record CustomerResponse(
        Long id,
        String name,
        String email,
        String document) {
}