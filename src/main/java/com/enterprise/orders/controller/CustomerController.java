package com.enterprise.orders.controller;

import com.enterprise.orders.dto.customer.CustomerRequest;
import com.enterprise.orders.dto.customer.CustomerResponse;
import com.enterprise.orders.entity.Customer;
import com.enterprise.orders.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/customers")
public class CustomerController {

        private final CustomerService customerService;

        public CustomerController(CustomerService customerService) {
                this.customerService = customerService;
        }

        @PostMapping
        public ResponseEntity<CustomerResponse> create(
                        @RequestBody @Valid CustomerRequest request) {

                Customer customer = new Customer(
                                request.name(),
                                request.email(),
                                request.document());

                Customer savedCustomer = customerService.create(customer);

                CustomerResponse response = new CustomerResponse(
                                savedCustomer.getId(),
                                savedCustomer.getName(),
                                savedCustomer.getEmail(),
                                savedCustomer.getDocument());

                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(response);
        }

        @GetMapping("/{id}")
        public ResponseEntity<CustomerResponse> findById(
                        @PathVariable Long id) {

                Customer customer = customerService.findById(id);

                CustomerResponse response = new CustomerResponse(
                                customer.getId(),
                                customer.getName(),
                                customer.getEmail(),
                                customer.getDocument());

                return ResponseEntity.ok(response);
        }
}