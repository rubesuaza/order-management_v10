package com.example.order_management.domain.exception;

/**
 * Thrown when an illegal state transition is attempted on an Order.
 * E.g., cancelling a SHIPPED order, or shipping a PENDING order.
 */
public class InvalidOrderStateException extends DomainException {

    public InvalidOrderStateException(String message) {
        super(message);
    }
}
