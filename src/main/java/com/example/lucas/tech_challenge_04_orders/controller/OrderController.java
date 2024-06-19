package com.example.lucas.tech_challenge_04_orders.controller;

import com.example.lucas.tech_challenge_04_orders.entity.dtos.CreateOrderDto;
import com.example.lucas.tech_challenge_04_orders.entity.Order;
import com.example.lucas.tech_challenge_04_orders.entity.dtos.UpdateStatusDto;
import com.example.lucas.tech_challenge_04_orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    ResponseEntity<Order> createOrder(@RequestBody CreateOrderDto createOrderDto) throws Exception {
        return new ResponseEntity<>(orderService.createOrder(createOrderDto), HttpStatus.CREATED);
    }

    @GetMapping
    ResponseEntity<List<Order>> getAllOrders() {
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    @GetMapping("/id/{id}")
    ResponseEntity<Optional<Order>> getOrderById(@PathVariable String id) {
        Optional<Order> order = orderService.getOrderById(id);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @PutMapping("/id/{id}")
    ResponseEntity<Optional<Order>> updateOrderStatus(@PathVariable String id, @RequestBody UpdateStatusDto updateStatusDto) throws Exception {
        Optional<Order> order = orderService.updateOrderStatus(id, updateStatusDto.isPaymentOk());

        return new ResponseEntity<>(order, HttpStatus.OK);
    }
}
