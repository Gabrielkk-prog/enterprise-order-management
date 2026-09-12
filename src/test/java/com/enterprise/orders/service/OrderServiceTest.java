package com.enterprise.orders.service;

import com.enterprise.orders.dto.order.OrderItemRequest;
import com.enterprise.orders.entity.*;
import com.enterprise.orders.service.exception.BusinessException;
import com.enterprise.orders.repository.OrderRepository;
import com.enterprise.orders.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private CustomerService customerService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;

    @Test
    void shouldCreateOrderAndReduceStock() {

        Customer customer = new Customer(
                "João Gabriel",
                "joao@email.com",
                "12345678900");

        Product product = new Product(
                "Notebook Pro",
                "Notebook para desenvolvimento",
                new BigDecimal("4500.00"));

        Stock stock = new Stock(product, 10);

        OrderItemRequest itemRequest = new OrderItemRequest(1L, 2);

        when(customerService.findById(1L))
                .thenReturn(customer);

        when(productService.findById(1L))
                .thenReturn(product);

        when(stockRepository.findByProductId(1L))
                .thenReturn(Optional.of(stock));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Order result = orderService.createOrder(
                1L,
                List.of(itemRequest));

        assertNotNull(result);
        // this calculation works like this, for example:
        // if the product costs US$ 4,500 x 2 = 4,900.
        assertEquals(
                new BigDecimal("9000.00"),
                result.getTotalAmount());

        assertEquals(
                8,
                stock.getQuantity());

        assertEquals(
                1,
                result.getItems().size());

        verify(orderRepository)
                .save(any(Order.class));
    }

    @Test
    void shouldRejectInactiveProduct() {

        Customer customer = new Customer(
                "João Gabriel",
                "joao@email.com",
                "12345678900");

        Product product = new Product(
                "Produto Inativo",
                "Produto de teste",
                new BigDecimal("100.00"));

        product.setActive(false);

        when(customerService.findById(1L))
                .thenReturn(customer);

        when(productService.findById(1L))
                .thenReturn(product);

        OrderItemRequest itemRequest = new OrderItemRequest(1L, 2);

        assertThrows(
                BusinessException.class,
                () -> orderService.createOrder(
                        1L,
                        List.of(itemRequest)));

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void shouldRejectInsufficientStock() {

        Customer customer = new Customer(
                "João Gabriel",
                "joao@email.com",
                "12345678900");

        Product product = new Product(
                "Notebook Pro",
                "Notebook",
                new BigDecimal("4500.00"));

        Stock stock = new Stock(product, 1);

        when(customerService.findById(1L))
                .thenReturn(customer);

        when(productService.findById(1L))
                .thenReturn(product);

        when(stockRepository.findByProductId(1L))
                .thenReturn(Optional.of(stock));

        OrderItemRequest itemRequest = new OrderItemRequest(1L, 2);

        assertThrows(
                BusinessException.class,
                () -> orderService.createOrder(
                        1L,
                        List.of(itemRequest)));

        assertEquals(
                1,
                stock.getQuantity());

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void shouldRejectProductWithoutStock() {

        Customer customer = new Customer(
                "João Gabriel",
                "joao@email.com",
                "12345678900");

        Product product = new Product(
                "Notebook Pro",
                "Notebook",
                new BigDecimal("4500.00"));

        when(customerService.findById(1L))
                .thenReturn(customer);

        when(productService.findById(1L))
                .thenReturn(product);

        when(stockRepository.findByProductId(1L))
                .thenReturn(Optional.empty());

        OrderItemRequest itemRequest = new OrderItemRequest(1L, 1);

        assertThrows(
                BusinessException.class,
                () -> orderService.createOrder(
                        1L,
                        List.of(itemRequest)));

        verify(orderRepository, never())
                .save(any(Order.class));
    }
}