package com.lavlinskaia.orderservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lavlinskaia.orderservice.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    
}
