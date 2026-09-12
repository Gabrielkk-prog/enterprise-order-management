package com.enterprise.orders.dto.product;

import java.math.BigDecimal;

public record ProductResponse(
                Long id,
                String name,
                String description,
                BigDecimal price,
                boolean active) {
}
