package com.rzodeczko.domain.exception;

import java.util.UUID;

public class InvoiceAlreadyExistsException extends RuntimeException {
    public InvoiceAlreadyExistsException(UUID orderId) {
        super("Invoice already exists for order: " + orderId);
    }
}
