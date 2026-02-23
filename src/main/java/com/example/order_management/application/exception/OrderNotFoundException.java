package com.example.order_management.application.exception;

/**
 * Thrown when an order cannot be found by ID.
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }
}
