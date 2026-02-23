package com.example.order_management.domain.exception;

/**
 * Thrown when monetary operations involve different currencies.
 */
public class CurrencyMismatchException extends DomainException {

    public CurrencyMismatchException(String message) {
        super(message);
    }
}
