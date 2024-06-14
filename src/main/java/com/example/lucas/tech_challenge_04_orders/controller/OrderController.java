package com.example.lucas.tech_challenge_04_orders.controller;

import com.example.lucas.tech_challenge_04_orders.entity.dtos.CreateOrderDto;
import com.example.lucas.tech_challenge_04_orders.entity.Order;
import com.example.lucas.tech_challenge_04_orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    ResponseEntity<Order> createOrder(@RequestBody CreateOrderDto createOrderDto) {
        var order = orderService.createOrder(createOrderDto);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
