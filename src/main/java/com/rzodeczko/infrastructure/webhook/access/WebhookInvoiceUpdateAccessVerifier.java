package com.rzodeczko.infrastructure.webhook.access;

import com.rzodeczko.infrastructure.configuration.properties.WebhookClientsProperties;
import com.rzodeczko.infrastructure.webhook.access.exception.UnauthorizedWebhookAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;

/**
 * Verifies whether a webhook client is enabled and uses a valid shared secret.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookInvoiceUpdateAccessVerifier {

    private final WebhookClientsProperties webhookClientsProperties;

    /**
     * Validates the client configuration and shared secret for the given webhook request.
     */
    public void verifyEnabledAndSharedSecret(String appName, String apiToken) {
        WebhookClientsProperties.ClientConfig clientConfig = webhookClientsProperties.clients().get(appName);

        if (Objects.isNull(clientConfig)) {
            log.warn("Received webhook for unknown client. appName={}", appName);
            throw new UnauthorizedWebhookAccessException("Unknown webhook client");
        }

        if (!Boolean.TRUE.equals(clientConfig.enabled())) {
            log.warn("Received webhook for disabled client. appName={}", appName);
            throw new UnauthorizedWebhookAccessException("Webhook client is disabled");
        }

        if (!isValidSecret(clientConfig.sharedSecret(), apiToken)) {
            log.warn("Received webhook with invalid API sharedSecret. appName={}", appName);
            throw new UnauthorizedWebhookAccessException("Invalid webhook sharedSecret");
        }
    }

    private boolean isValidSecret(String expectedToken, String actualToken) {
        if (expectedToken == null || actualToken == null) {
            return false;
        }

        return MessageDigest.isEqual(
                expectedToken.getBytes(StandardCharsets.UTF_8),
                actualToken.getBytes(StandardCharsets.UTF_8)
        );
    }
}
