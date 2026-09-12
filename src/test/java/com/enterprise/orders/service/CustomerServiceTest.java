package com.enterprise.orders.service;

import com.enterprise.orders.entity.Customer;
import com.enterprise.orders.service.exception.BusinessException;
import com.enterprise.orders.service.exception.ResourceNotFoundException;
import com.enterprise.orders.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldCreateCustomerSuccessfully() {

        Customer customer = new Customer(
                "João Gabriel",
                "joao@email.com",
                "12345678900");

        when(customerRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.empty());

        when(customerRepository.existsByDocument(customer.getDocument()))
                .thenReturn(false);

        when(customerRepository.save(customer))
                .thenReturn(customer);

        Customer result = customerService.create(customer);

        assertNotNull(result);
        assertEquals("João Gabriel", result.getName());

        verify(customerRepository).save(customer);
    }

    @Test
    void shouldRejectDuplicateEmail() {

        Customer customer = new Customer(
                "João Gabriel",
                "joao@email.com",
                "12345678900");

        when(customerRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.of(customer));

        assertThrows(
                BusinessException.class,
                () -> customerService.create(customer));

        verify(customerRepository, never())
                .save(any(Customer.class));
    }

    @Test
    void shouldRejectDuplicateDocument() {

        Customer customer = new Customer(
                "João Gabriel",
                "joao@email.com",
                "12345678900");

        when(customerRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.empty());

        when(customerRepository.existsByDocument(customer.getDocument()))
                .thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> customerService.create(customer));

        verify(customerRepository, never())
                .save(any(Customer.class));
    }

    @Test
    void shouldFindCustomerById() {

        Customer customer = new Customer(
                "João Gabriel",
                "joao@email.com",
                "12345678900");

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        Customer result = customerService.findById(1L);

        assertNotNull(result);
        assertEquals("João Gabriel", result.getName());

        verify(customerRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenCustomerDoesNotExist() {

        when(customerRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.findById(999L));
    }
}