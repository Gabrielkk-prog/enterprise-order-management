package com.enterprise.orders.dto.product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(

                @NotBlank(message = "Nome é obrigatório") @Size(max = 150) String name,

                @Size(max = 500) String description,

                @NotNull(message = "Preço é obrigatório") @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero") BigDecimal price) {
}