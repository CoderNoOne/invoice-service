package com.rzodeczko.application.exception;

public abstract class TaxSystemException extends RuntimeException {

    protected TaxSystemException(String message) {
        super(message);
    }

    protected TaxSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}