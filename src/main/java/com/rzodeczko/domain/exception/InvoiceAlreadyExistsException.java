package com.rzodeczko.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when an invoice already exists for a given order or identifier.
 */
public class InvoiceAlreadyExistsException extends RuntimeException {
    public InvoiceAlreadyExistsException(UUID orderId) {
        super("Invoice already exists for order: " + orderId);
    }
}
