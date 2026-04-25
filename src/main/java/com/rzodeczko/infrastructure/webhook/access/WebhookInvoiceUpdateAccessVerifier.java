package com.rzodeczko.infrastructure.webhook.access;

import com.rzodeczko.infrastructure.webhook.access.exception.UnauthorizedWebhookAccessException;
import com.rzodeczko.infrastructure.configuration.properties.WebhookApiTokens;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookInvoiceUpdateAccessVerifier {
    private final WebhookApiTokens webhookApiTokens;

    public void verifySharedSecret(String appName, String apiToken) {
        webhookApiTokens.updateInvoiceApi().entrySet().stream()
                .filter(e -> e.getKey().equals(appName))
                .filter(e -> e.getValue().equals(apiToken))
                .findAny()
                .orElseThrow(() -> {
                    log.warn("Received webhook with invalid API token. appName={}", appName);
                    return new UnauthorizedWebhookAccessException("Invalid webhook token");
                });
    }
}
