package com.rzodeczko.infrastructure.webhook.access.exception;

/**
 * Thrown when a webhook request is not authorized.
 */
public class UnauthorizedWebhookAccessException extends RuntimeException {
    public UnauthorizedWebhookAccessException(String message) {
        super(message);
    }
}
