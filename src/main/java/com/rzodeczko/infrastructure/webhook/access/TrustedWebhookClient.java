package com.rzodeczko.infrastructure.webhook.access;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

/**
 * Supported trusted webhook clients.
 */
@RequiredArgsConstructor
@Getter
public enum TrustedWebhookClient {
    FAKTUROWNIA("fakturownia");

    private final String clientId;

    public static Optional<TrustedWebhookClient> from(String clientAppId) {
        return Arrays.stream(values())
                .filter(e -> e.clientId.equals(clientAppId))
                .findFirst();
    }
}
