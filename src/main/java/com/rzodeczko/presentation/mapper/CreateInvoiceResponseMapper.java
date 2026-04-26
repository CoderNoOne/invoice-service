package com.rzodeczko.presentation.mapper;

import com.rzodeczko.infrastructure.usecase.dto.InvoiceIssueResult;
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
                            "Invoice created"
                    ));

            case InvoiceIssueResult.PendingConfirmation pending -> ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new CreateInvoiceResponseDto(
                            pending.invoiceId(),
                            "PENDING_CONFIRMATION",
                            "Invoice creation could not be confirmed yet"
                    ));

            case InvoiceIssueResult.Duplicated duplicated -> ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new CreateInvoiceResponseDto(
                            duplicated.invoiceId(),
                            "DUPLICATED",
                            "Duplicate invoice detected in Fakturownia"
                    ));
        };
    }
}