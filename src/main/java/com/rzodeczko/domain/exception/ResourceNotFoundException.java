package com.rzodeczko.domain.exception;

/**
 * Exception thrown when a requested resource is not found in the system.
 */
public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
