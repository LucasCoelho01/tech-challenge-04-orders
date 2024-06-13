package com.example.lucas.tech_challenge_04_orders.controller;

import com.example.lucas.tech_challenge_04_orders.entity.CustomerResponse;
import com.example.lucas.tech_challenge_04_orders.entity.ProductResponse;
import com.example.lucas.tech_challenge_04_orders.entity.dtos.CreateOrderDto;
import com.example.lucas.tech_challenge_04_orders.entity.dtos.Order;
import com.example.lucas.tech_challenge_04_orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    ResponseEntity<Order> createOrder(@RequestBody CreateOrderDto createOrderDto) {
        Order order = new Order();

        CustomerResponse customerResponse = orderService.getCustomer(createOrderDto.cpf());
        order.setCustomer(customerResponse.getUsername());

        List<String> productNames = new ArrayList<>();

        createOrderDto.products().forEach(product -> {
            ProductResponse productResponse = orderService.getProduct(product);
            productNames.add(productResponse.getName());
        });

        order.setProducts(productNames);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
