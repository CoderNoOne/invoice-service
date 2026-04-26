package com.rzodeczko.infrastructure.usecase.dto;

import java.util.UUID;

public sealed interface InvoiceIssueResult permits
        InvoiceIssueResult.Issued,
        InvoiceIssueResult.PendingConfirmation,
        InvoiceIssueResult.Duplicated {


    UUID invoiceId();

    record Issued(UUID invoiceId) implements InvoiceIssueResult {
    }

    record PendingConfirmation(UUID invoiceId) implements InvoiceIssueResult {
    }

    record Duplicated(UUID invoiceId) implements InvoiceIssueResult {
    }
}
