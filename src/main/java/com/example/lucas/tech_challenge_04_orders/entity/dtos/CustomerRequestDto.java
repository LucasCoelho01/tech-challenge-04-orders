package com.example.lucas.tech_challenge_04_orders.entity.dtos;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class CustomerRequestDto {
    private String orderId;
    private String cpf;
    private List<String> productNames;

    public CustomerRequestDto(){}

    public CustomerRequestDto(String orderId, String cpf, List<String> productNames){
        this.orderId = orderId;
        this.cpf = cpf;
        this.productNames = productNames;
    }
}

