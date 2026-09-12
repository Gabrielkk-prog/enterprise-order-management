package com.enterprise.orders.dto.customer;

public record CustomerRequest(
        String name,
        String email,
        String document) {
}