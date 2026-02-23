package com.example.order_management.domain.model;

/**
 * Represents the lifecycle states of an Order.
 */
public enum OrderStatus {
    PENDING,
    PAID,
    SHIPPED,
    DELIVERED,
    CANCELLED
}
