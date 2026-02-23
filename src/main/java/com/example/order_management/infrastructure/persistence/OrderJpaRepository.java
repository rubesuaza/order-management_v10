package com.example.order_management.infrastructure.persistence;

import com.example.order_management.infrastructure.persistence.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
}
