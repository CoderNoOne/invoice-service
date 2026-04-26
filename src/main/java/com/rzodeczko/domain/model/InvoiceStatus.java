package com.rzodeczko.domain.model;

/**
 * Enumeration of possible statuses for an invoice.
 */
public enum InvoiceStatus {
    DRAFT,
    ISSUING,
    ISSUED,
    DUPLICATED,
    UNKNOWN
}
