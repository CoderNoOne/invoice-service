package com.rzodeczko.infrastructure.webhook.access.ratelimiter;

public interface ClientRateLimiter {
    void check(String clientId);
}
