package com.example.lucas.tech_challenge_04_orders.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductResponse {
    private UUID id;
    private String name;
    private BigDecimal price;
    private String category;
}
