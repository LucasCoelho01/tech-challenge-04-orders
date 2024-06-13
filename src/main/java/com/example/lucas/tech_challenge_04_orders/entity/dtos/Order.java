package com.example.lucas.tech_challenge_04_orders.entity.dtos;

import lombok.Data;

import java.util.List;

@Data
public class Order {
    private String customer;
    private List<String> products;
}
