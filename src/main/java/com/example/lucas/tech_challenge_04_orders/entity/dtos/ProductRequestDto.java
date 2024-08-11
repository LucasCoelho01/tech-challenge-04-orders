package com.example.lucas.tech_challenge_04_orders.entity.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class ProductRequestDto {
    private String orderId;
    private List<String> products;

    public ProductRequestDto() {}

    public ProductRequestDto(String orderId, List<String> products) {
        this.orderId = orderId;
        this.products = products;
    }
}
