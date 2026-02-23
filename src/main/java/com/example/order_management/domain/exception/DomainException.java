package com.example.order_management.domain.exception;

/**
 * Base exception for all domain-level errors.
 * Domain layer must remain pure - no framework dependencies.
 */
public abstract class DomainException extends RuntimeException {

    protected DomainException(String message) {
        super(message);
    }

    protected DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}
