package com.example.lucas.tech_challenge_04_orders.repository;

import com.example.lucas.tech_challenge_04_orders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
}
