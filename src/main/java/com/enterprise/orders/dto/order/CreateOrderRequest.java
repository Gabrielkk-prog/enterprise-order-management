package com.enterprise.orders.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(

                @NotNull(message = "Cliente é obrigatório") Long customerId,

                @NotEmpty(message = "O pedido deve possuir pelo menos um item") List<@Valid OrderItemRequest> items) {
}