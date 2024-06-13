package com.example.lucas.tech_challenge_04_orders.entity.dtos;

import java.util.List;

public record CreateOrderDto(String cpf, List<String> products) {
}
