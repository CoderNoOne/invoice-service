package com.rzodeczko.application.port.input;

import java.util.UUID;

public sealed interface InvoiceIssueResult
        permits InvoiceIssueResult.Issued,
        InvoiceIssueResult.PendingConfirmation,
        InvoiceIssueResult.ReconciliationRequired {

    UUID invoiceId();

    record Issued(UUID invoiceId) implements InvoiceIssueResult {
    }

    record PendingConfirmation(UUID invoiceId) implements InvoiceIssueResult {
    }

    record ReconciliationRequired(UUID invoiceId) implements InvoiceIssueResult {
    }
}