package com.enterprise.orders.service;

import com.enterprise.orders.entity.Customer;
import com.enterprise.orders.repository.CustomerRepository;
import com.enterprise.orders.service.exception.BusinessException;
import com.enterprise.orders.service.exception.ResourceNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public Customer create(Customer customer) {

        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new BusinessException(
                    "Já existe um cliente com este e-mail.");
        }

        if (customerRepository.existsByDocument(customer.getDocument())) {
            throw new BusinessException(
                    "Já existe um cliente com este documento.");
        }

        return customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public Customer findById(Long id) {

        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente não encontrado: " + id));
    }
}