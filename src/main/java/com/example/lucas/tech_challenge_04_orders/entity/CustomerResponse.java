package com.example.lucas.tech_challenge_04_orders.entity;

import lombok.Data;

import java.util.UUID;

@Data
public class CustomerResponse {
    private UUID id;
    private String username;
    private String cpf;
    private String email;

    public CustomerResponse(){}
}

