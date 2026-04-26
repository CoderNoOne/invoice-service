package com.rzodeczko.presentation.mapper;

import com.rzodeczko.application.port.input.InvoiceIssueResult;
import com.rzodeczko.presentation.dto.CreateInvoiceResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class CreateInvoiceResponseMapper {

    public ResponseEntity<CreateInvoiceResponseDto> toResponse(InvoiceIssueResult result) {
        return switch (result) {
            case InvoiceIssueResult.Issued issued -> ResponseEntity.status(HttpStatus.CREATED)
                    .body(new CreateInvoiceResponseDto(
                            issued.invoiceId(),
                            "ISSUED",
                            "Invoice issued"
                    ));

            case InvoiceIssueResult.PendingConfirmation pending -> ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new CreateInvoiceResponseDto(
                            pending.invoiceId(),
                            "PENDING_CONFIRMATION",
                            "Invoice processing accepted but final confirmation is pending"
                    ));

            case InvoiceIssueResult.ReconciliationRequired reconciliation -> ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new CreateInvoiceResponseDto(
                            reconciliation.invoiceId(),
                            "RECONCILIATION_REQUIRED",
                            "Multiple matching invoices detected in tax system. Reconciliation required"
                    ));
        };
    }
}