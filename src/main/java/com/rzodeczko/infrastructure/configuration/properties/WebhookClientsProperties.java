package com.rzodeczko.infrastructure.configuration.properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;
import java.util.Objects;

/**
 * Webhook client settings loaded from the {@code webhooks} properties prefix.
 * The clients map defaults to empty and is stored as an unmodifiable copy.
 *
 * @param clients configured webhook clients
 */
@ConfigurationProperties(prefix = "webhooks")
public record WebhookClientsProperties(
        Map<String, ClientConfig> clients) {

    public WebhookClientsProperties {
        clients = Objects.isNull(clients) ? Map.of() : Map.copyOf(clients);
    }

    /**
     * Configuration for a single webhook client.
     * {@code enabled} defaults to {@code true}; {@code sharedSecret} is normalized to an empty string.
     *
     * @param enabled                whether the client is active
     * @param sharedSecret           shared secret used to authenticate webhook requests
     * @param requestsPerMinuteLimit maximum allowed requests per minute
     */
    public record ClientConfig(
            Boolean enabled,
            String sharedSecret,
            int requestsPerMinuteLimit
    ) {

        public ClientConfig {
            enabled = Objects.isNull(enabled) || enabled;
            sharedSecret = StringUtils.defaultString(sharedSecret);
        }
    }
}