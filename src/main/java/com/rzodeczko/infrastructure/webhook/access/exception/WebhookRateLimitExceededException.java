package com.rzodeczko.infrastructure.webhook.access.exception;

public class WebhookRateLimitExceededException extends RuntimeException {
    public WebhookRateLimitExceededException(String message) {
        super(message);
    }
}
