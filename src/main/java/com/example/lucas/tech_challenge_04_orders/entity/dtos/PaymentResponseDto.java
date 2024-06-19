package com.example.lucas.tech_challenge_04_orders.entity.dtos;

import lombok.Data;

@Data
public class PaymentResponseDto {
    private String id;
    private String orderId;
    private boolean paymentOk;
}
