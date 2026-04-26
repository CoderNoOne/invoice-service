package com.rzodeczko.application.exception;

public class TaxSystemTemporaryException extends TaxSystemException {

    public TaxSystemTemporaryException(String message) {
        super(message);
    }

    public TaxSystemTemporaryException(String message, Throwable cause) {
        super(message, cause);
    }
}