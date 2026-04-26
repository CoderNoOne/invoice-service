package com.rzodeczko.application.port.input;


public interface GenerateInvoiceUseCase {
    InvoiceIssueResult generate(GenerateInvoiceCommand command);
}
