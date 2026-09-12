package com.enterprise.orders.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// some inserts rulles 
public record CustomerRequest(

                @NotBlank(message = "Nome é obrigatório") @Size(max = 150, message = "Nome deve ter no máximo 150 caracteres") String name,

                @NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail inválido") String email,

                @NotBlank(message = "Documento é obrigatório") @Size(min = 11, max = 20, message = "Documento inválido") String document) {
}