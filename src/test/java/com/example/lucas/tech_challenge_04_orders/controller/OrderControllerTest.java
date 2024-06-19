package com.example.lucas.tech_challenge_04_orders.controller;

import com.example.lucas.tech_challenge_04_orders.entity.Order;
import com.example.lucas.tech_challenge_04_orders.entity.dtos.CreateOrderDto;
import com.example.lucas.tech_challenge_04_orders.entity.dtos.UpdateStatusDto;
import com.example.lucas.tech_challenge_04_orders.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private Order order;
    private CreateOrderDto createOrderDto;
    private UpdateStatusDto updateStatusDto;

    @BeforeEach
    void setUp() {
        createOrderDto = new CreateOrderDto("12345678901", Arrays.asList("Product1", "Product2"));

        order = new Order();
        order.setId("1");
        order.setCustomer("John Doe");
        order.setProducts(Arrays.asList("Product1", "Product2"));
        order.setStatus("Recebido");
        order.setTimestamp("2024-01-01T10:00:00");
        order.setTotalPrice(BigDecimal.valueOf(100.00));

        updateStatusDto = new UpdateStatusDto(true);
    }

    @Test
    void createOrder_success() throws Exception {
        when(orderService.createOrder(any(CreateOrderDto.class))).thenReturn(order);

        ResponseEntity<Order> responseEntity = orderController.createOrder(createOrderDto);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(order, responseEntity.getBody());

        verify(orderService, times(1)).createOrder(any(CreateOrderDto.class));
    }

    @Test
    void getAllOrders_success() {
        when(orderService.getAllOrders()).thenReturn(List.of(order));

        ResponseEntity<List<Order>> responseEntity = orderController.getAllOrders();

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(1, responseEntity.getBody().size());
        assertEquals(order, responseEntity.getBody().get(0));

        verify(orderService, times(1)).getAllOrders();
    }

    @Test
    void getOrderById_success() {
        when(orderService.getOrderById("1")).thenReturn(Optional.of(order));

        ResponseEntity<Optional<Order>> responseEntity = orderController.getOrderById("1");

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(order, responseEntity.getBody().get());

        verify(orderService, times(1)).getOrderById("1");
    }

    @Test
    void updateOrderStatus_success() throws Exception {
        when(orderService.updateOrderStatus("1", true)).thenReturn(Optional.of(order));

        ResponseEntity<Optional<Order>> responseEntity = orderController.updateOrderStatus("1", updateStatusDto);

        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(order, responseEntity.getBody().get());

        verify(orderService, times(1)).updateOrderStatus("1", true);
    }
}
