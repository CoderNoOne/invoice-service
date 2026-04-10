package com.rzodeczko.infrastructure.usecase;


import com.rzodeczko.application.exception.EmptyPdfResponseException;
import com.rzodeczko.application.port.input.GenerateInvoiceCommand;
import com.rzodeczko.application.port.input.GenerateInvoiceUseCase;
import com.rzodeczko.application.port.input.GetInvoicePdfUseCase;
import com.rzodeczko.application.port.output.TaxSystemPort;
import com.rzodeczko.application.service.InvoiceService;
import com.rzodeczko.domain.exception.InvoiceAlreadyExistsException;
import com.rzodeczko.domain.exception.InvoiceNotIssuedException;
import com.rzodeczko.domain.exception.ResourceNotFoundException;
import com.rzodeczko.infrastructure.transaction.InvoiceTransactionBoundary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Orkiestrator - zarzadza kolejnoscia krokow i granicjami transakcji
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceUseCaseImpl implements GenerateInvoiceUseCase, GetInvoicePdfUseCase {

    private final InvoiceTransactionBoundary invoiceTransactionBoundary;
    private final InvoiceService invoiceService;
    private final TaxSystemPort taxSystemPort;

    @Override
    public UUID generate(GenerateInvoiceCommand command) {
        log.info("Generating invoice. orderId={}", command.orderId());

        if (invoiceTransactionBoundary.existsByOrderId(command.orderId())) {
            throw new InvoiceAlreadyExistsException(command.orderId());
        }

        Invoice invoice = invoiceService.buildInvoice(command);

        invoiceTransactionBoundary.saveNewInvoice(invoice);
        log.info("Invoice DRAFT saved. invoiceId={}, orderId={}", invoice.getId(), command.orderId());

        String externalId = taxSystemPort.issueInvoice(invoice);
        log.info("Invoice issued to Fakturownia. invoiceId={}, externalId={}", invoice.getId(), externalId);

        invoiceTransactionBoundary.markInvoiceAsIssued(invoice, externalId);
        log.info("Invoice ISSUED saved. invoiceId={}", invoice.getId());

        return invoice.getId();
    }

    @Override
    public byte[] getPdf(UUID invoiceId) {
        log.info("Getting PDF. invoiceId={}", invoiceId);

        Invoice invoice = invoiceTransactionBoundary.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));

        if (!invoice.isIssued()) {
            throw new InvoiceNotIssuedException(invoiceId);
        }

        return invoiceTransactionBoundary.findPdfContent(invoiceId)
                .orElseGet(() -> {
                    log.debug("PDF not in cache. Fetching from Fakturownia. invoiceId={}", invoiceId);
                    byte[] pdf = taxSystemPort.getPdf(invoice.getExternalId());

                    if (pdf == null || pdf.length == 0) {
                        throw new EmptyPdfResponseException(invoice.getExternalId());
                    }

                    invoiceTransactionBoundary.savePdfContent(invoiceId, pdf);
                    log.info("PDF cached. invoiceId={}", invoiceId);

                    return pdf;
                });
    }
}
