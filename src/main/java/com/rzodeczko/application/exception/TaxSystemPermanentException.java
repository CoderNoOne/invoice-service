package com.rzodeczko.application.exception;

public class TaxSystemPermanentException extends TaxSystemException {

    public TaxSystemPermanentException(String message) {
        super(message);
    }

    public TaxSystemPermanentException(String message, Throwable cause) {
        super(message, cause);
    }
}