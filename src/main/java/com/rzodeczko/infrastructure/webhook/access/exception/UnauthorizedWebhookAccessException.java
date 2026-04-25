package com.rzodeczko.infrastructure.webhook.access.exception;

public class UnauthorizedWebhookAccessException extends RuntimeException {
    public UnauthorizedWebhookAccessException(String message) {
        super(message);
    }
}
