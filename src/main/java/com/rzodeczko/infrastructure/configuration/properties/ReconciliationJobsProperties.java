package com.rzodeczko.infrastructure.configuration.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "reconciliation.jobs")
public record ReconciliationJobsProperties(
        JobConfig duplicateInvoiceRemediation
) {
    public record JobConfig(Boolean enabled) {
    }
}
