package com.rzodeczko.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "invoices")
public record InvoicesProperties(String url, String token) {
}
