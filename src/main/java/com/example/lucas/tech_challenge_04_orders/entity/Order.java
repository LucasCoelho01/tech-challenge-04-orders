package com.example.lucas.tech_challenge_04_orders.entity;

import jakarta.persistence.*;
import lombok.Data;


import java.math.BigDecimal;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String customer;
    private List<String> products;
    private String status;
    private String timestamp;
    private BigDecimal totalPrice;
}
