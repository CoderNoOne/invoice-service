package com.rzodeczko.infrastructure.usecase;

import com.rzodeczko.application.exception.EmptyPdfResponseException;
import com.rzodeczko.application.exception.TaxSystemPermanentException;
import com.rzodeczko.application.exception.TaxSystemTemporaryException;
import com.rzodeczko.application.port.input.GenerateInvoiceCommand;
import com.rzodeczko.application.port.input.GenerateInvoiceUseCase;
import com.rzodeczko.application.port.input.GetInvoicePdfUseCase;
import com.rzodeczko.application.port.input.InvoiceIssueResult;
import com.rzodeczko.application.port.output.TaxSystemPort;
import com.rzodeczko.application.service.InvoiceService;
import com.rzodeczko.domain.exception.InvoiceNotIssuedException;
import com.rzodeczko.domain.exception.ResourceNotFoundException;
import com.rzodeczko.domain.model.Invoice;
import com.rzodeczko.infrastructure.persistence.DataIntegrityViolationClassifier;
import com.rzodeczko.infrastructure.reconciliation.InvoiceReconciliationService;
import com.rzodeczko.infrastructure.transaction.InvoiceTransactionBoundary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceUseCaseImpl implements GenerateInvoiceUseCase, GetInvoicePdfUseCase {

    private final InvoiceTransactionBoundary invoiceTransactionBoundary;
    private final InvoiceService invoiceService;
    private final TaxSystemPort taxSystemPort;
    private final InvoiceReconciliationService invoiceReconciliationService;
    private final DataIntegrityViolationClassifier violationClassifier;

    @Override
    public InvoiceIssueResult generate(GenerateInvoiceCommand command) {
        log.info("Generating invoice. orderId={}", command.orderId());

        try {
            Invoice invoice = invoiceTransactionBoundary.saveNewInvoice(invoiceService.buildInvoice(command));
            return handleInvoice(invoice);
        } catch (DataIntegrityViolationException e) {
            if (!violationClassifier.isOrderIdUniqueViolation(e)) {
                throw e;
            }

            Invoice invoice = invoiceTransactionBoundary.findByOrderId(command.orderId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Invoice creation failed for orderId=" + command.orderId(), e
                    ));

            log.info("Invoice already exists locally. invoiceId={}, orderId={}",
                    invoice.getId(), invoice.getOrderId());

            return handleInvoice(invoice);
        }
    }

    private InvoiceIssueResult handleInvoice(Invoice invoice) {
        if (invoice.isIssued()) {
            log.info("Invoice already issued. invoiceId={}, orderId={}",
                    invoice.getId(), invoice.getOrderId());
            return new InvoiceIssueResult.Issued(invoice.getId());
        }

        if (invoice.isReconciliationRequired()) {
            log.warn("Invoice requires reconciliation. invoiceId={}, orderId={}",
                    invoice.getId(), invoice.getOrderId());
            return new InvoiceIssueResult.ReconciliationRequired(invoice.getId());
        }

        if (invoice.isIssueUnknown()) {
            log.info("Invoice is pending asynchronous confirmation. invoiceId={}, orderId={}, status={}",
                    invoice.getId(), invoice.getOrderId(), invoice.getStatus());
            return new InvoiceIssueResult.PendingConfirmation(invoice.getId());
        }

        if (invoice.isDraft()) {
            invoiceTransactionBoundary.markAsIssuing(invoice);
        }

        if (!invoice.isIssuing()) {
            throw new IllegalStateException(
                    "Unsupported invoice state. invoiceId=" + invoice.getId() + ", orderId=" + invoice.getOrderId()
            );
        }

        return recoverOrIssue(invoice);
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
                    log.debug("PDF not in cache. Fetching from tax system. invoiceId={}", invoiceId);

                    byte[] pdf = taxSystemPort.getPdf(invoice.getExternalId());
                    if (pdf == null || pdf.length == 0) {
                        throw new EmptyPdfResponseException(invoice.getExternalId());
                    }

                    invoiceTransactionBoundary.savePdfContent(invoiceId, pdf);
                    log.info("PDF cached. invoiceId={}", invoiceId);
                    return pdf;
                });
    }

    private InvoiceIssueResult recoverOrIssue(Invoice invoice) {
        log.info("Recover or issue invoice. invoiceId={}, orderId={}",
                invoice.getId(), invoice.getOrderId());

        String orderId = invoice.getOrderId().toString();

        Optional<InvoiceIssueResult> recovered = recoverIfExists(invoice, orderId);
        return recovered.orElseGet(() -> tryIssueAndRecover(invoice, orderId));

    }

    private Optional<InvoiceIssueResult> recoverIfExists(Invoice invoice, String orderId) {
        return invoiceReconciliationService.reconcileFromExisting(
                invoice,
                taxSystemPort.findByOrderId(orderId)
        );
    }

    private InvoiceIssueResult tryIssueAndRecover(Invoice invoice, String orderId) {
        try {
            String externalId = taxSystemPort.issueInvoice(invoice);
            invoiceTransactionBoundary.markInvoiceAsIssued(invoice, externalId);
            log.info("Invoice issued. invoiceId={}, orderId={}, externalId={}",
                    invoice.getId(), invoice.getOrderId(), externalId);

            return new InvoiceIssueResult.Issued(invoice.getId());
        } catch (TaxSystemTemporaryException ex) {
            return recoverAfterTemporaryFailure(invoice, orderId, ex);
        } catch (TaxSystemPermanentException ex) {
            invoiceTransactionBoundary.markIssueFailed(invoice);
            throw ex;
        }
    }

    private InvoiceIssueResult recoverAfterTemporaryFailure(
            Invoice invoice,
            String orderId,
            TaxSystemTemporaryException exception
    ) {
        return recoverIfExists(invoice, orderId)
                .orElseGet(() -> {
                    invoiceTransactionBoundary.markIssueUnknown(invoice);

                    log.warn("Invoice issue result unknown. invoiceId={}, orderId={}",
                            invoice.getId(), invoice.getOrderId(), exception);

                    return new InvoiceIssueResult.PendingConfirmation(invoice.getId());
                });
    }
}