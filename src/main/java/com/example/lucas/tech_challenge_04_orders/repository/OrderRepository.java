package com.example.lucas.tech_challenge_04_orders.repository;

import com.example.lucas.tech_challenge_04_orders.entity.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends MongoRepository<Order, UUID> {
}
