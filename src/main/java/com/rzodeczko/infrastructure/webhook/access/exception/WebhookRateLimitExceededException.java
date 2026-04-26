package com.rzodeczko.infrastructure.webhook.access.exception;

/**
 * Thrown when a webhook request exceeds the allowed rate limit.
 */
public class WebhookRateLimitExceededException extends RuntimeException {
    public WebhookRateLimitExceededException(String message) {
        super(message);
    }
}
