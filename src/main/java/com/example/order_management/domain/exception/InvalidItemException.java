package com.example.order_management.domain.exception;

/**
 * Thrown when OrderItem validation fails (e.g., quantity <= 0, negative unitPrice).
 */
public class InvalidItemException extends DomainException {

    public InvalidItemException(String message) {
        super(message);
    }
}
