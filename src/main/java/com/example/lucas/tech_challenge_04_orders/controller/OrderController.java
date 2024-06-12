package com.example.lucas.tech_challenge_04_orders.controller;

import com.example.lucas.tech_challenge_04_orders.entity.CustomerResponse;
import com.example.lucas.tech_challenge_04_orders.entity.dtos.CreateOrderDto;
import com.example.lucas.tech_challenge_04_orders.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    ResponseEntity<CustomerResponse> getAllCustomers(@RequestBody CreateOrderDto createOrderDto) {
        return new ResponseEntity<>(orderService.getCustomer(createOrderDto.cpf()), HttpStatus.OK);
    }
}
