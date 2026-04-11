package com.rzodeczko.application.exception;

/**
 * Exception thrown when an error occurs in the external tax system integration.
 */
public class ExternalTaxSystemException extends RuntimeException {
    public ExternalTaxSystemException(String message) {
        super(message);
    }

    public ExternalTaxSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
