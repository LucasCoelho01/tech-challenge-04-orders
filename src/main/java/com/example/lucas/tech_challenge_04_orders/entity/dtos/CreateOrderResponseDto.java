package com.example.lucas.tech_challenge_04_orders.entity.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderResponseDto {
    private String orderId;
    private String message;

    public CreateOrderResponseDto(){}

    public CreateOrderResponseDto(String orderId, String message){
        this.orderId = orderId;
        this.message = message;
    }
}

