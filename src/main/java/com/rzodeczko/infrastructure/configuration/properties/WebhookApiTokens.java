package com.rzodeczko.infrastructure.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;

import java.util.Map;

@ConfigurationProperties(prefix = "webhooks-tokens")
public record WebhookApiTokens(
        @Name("update-invoice-api") Map<String, String> updateInvoiceApi) {
}
