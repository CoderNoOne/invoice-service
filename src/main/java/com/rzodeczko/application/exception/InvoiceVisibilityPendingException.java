package com.rzodeczko.application.exception;

/**
 * Indicates that the invoice has been issued, but its final visibility in the external system
 * cannot be confirmed yet.
 */
public class InvoiceVisibilityPendingException extends RuntimeException {
    public InvoiceVisibilityPendingException(String message) {
        super(message);
    }
}
