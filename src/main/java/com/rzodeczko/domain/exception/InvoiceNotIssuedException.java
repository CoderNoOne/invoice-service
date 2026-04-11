package com.rzodeczko.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when an invoice has not been issued yet but is required for an operation.
 */
public class InvoiceNotIssuedException extends RuntimeException {
    public InvoiceNotIssuedException(UUID invoiceId) {
        super("Invoice has not been issued to external system yet. invoiceId=" + invoiceId);
    }
}
