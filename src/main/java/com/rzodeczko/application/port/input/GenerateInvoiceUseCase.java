package com.rzodeczko.application.port.input;

import com.rzodeczko.infrastructure.usecase.dto.InvoiceIssueResult;


public interface GenerateInvoiceUseCase {
    InvoiceIssueResult generate(GenerateInvoiceCommand command);
}
