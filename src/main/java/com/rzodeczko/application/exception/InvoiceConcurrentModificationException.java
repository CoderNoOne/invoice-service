package com.rzodeczko.application.exception;

import java.util.UUID;

/**
 * Exception thrown when a concurrent modification is detected for an invoice.
 */
public class InvoiceConcurrentModificationException extends RuntimeException {
    public InvoiceConcurrentModificationException(UUID invoiceId) {
        super("Invoice was modified concurrently. invoiceId=" + invoiceId);
    }
}
