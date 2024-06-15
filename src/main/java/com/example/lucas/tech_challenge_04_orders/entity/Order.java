package com.example.lucas.tech_challenge_04_orders.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String customer;
    private List<String> products;
    private String status;
    private String timestamp;
}
