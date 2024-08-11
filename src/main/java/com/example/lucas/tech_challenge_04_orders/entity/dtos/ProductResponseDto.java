package com.example.lucas.tech_challenge_04_orders.entity.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ProductResponseDto {
    private String orderId;
    private List<Product> products;
}
