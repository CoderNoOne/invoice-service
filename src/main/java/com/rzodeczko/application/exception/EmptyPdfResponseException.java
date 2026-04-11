package com.rzodeczko.application.exception;

/**
 * Exception thrown when the PDF response is empty during invoice processing.
 */
public class EmptyPdfResponseException extends RuntimeException {
    public EmptyPdfResponseException(String externalId) {
        super("External system returned empty PDF for externalId=" + externalId);
    }
}
