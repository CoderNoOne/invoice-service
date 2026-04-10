package com.rzodeczko.domain.exception;

import java.util.UUID;


public class InvoiceNotIssuedException extends RuntimeException {
    public InvoiceNotIssuedException(UUID invoiceId) {
        super("Invoice has not been issued to external system yet. invoiceId=" + invoiceId);
    }
}
