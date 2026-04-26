package com.rzodeczko.infrastructure.usecase;


import com.rzodeczko.application.exception.EmptyPdfResponseException;
import com.rzodeczko.application.port.input.GenerateInvoiceCommand;
import com.rzodeczko.application.port.input.GenerateInvoiceUseCase;
import com.rzodeczko.application.port.input.GetInvoicePdfUseCase;
import com.rzodeczko.application.port.output.TaxSystemPort;
import com.rzodeczko.application.service.InvoiceService;
import com.rzodeczko.domain.exception.InvoiceNotIssuedException;
import com.rzodeczko.domain.exception.ResourceNotFoundException;
import com.rzodeczko.domain.model.Invoice;
import com.rzodeczko.infrastructure.transaction.InvoiceTransactionBoundary;
import com.rzodeczko.infrastructure.usecase.dto.InvoiceIssueResult;
import com.rzodeczko.presentation.dto.FakturowniaGetInvoiceDto;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class InvoiceUseCaseImpl implements GenerateInvoiceUseCase, GetInvoicePdfUseCase {

    private final InvoiceTransactionBoundary invoiceTransactionBoundary;
    private final InvoiceService invoiceService;
    private final TaxSystemPort taxSystemPort;

    /**
     * Generates or resumes invoice processing for the given order.
     *
     * @param command command containing the target order identifier
     * @return local invoice identifier
     * @throws IllegalStateException when the invoice cannot be created or reloaded
     */
    @Override
    public InvoiceIssueResult generate(GenerateInvoiceCommand command) {
        log.info(">>> Generating invoice. orderId={}", command.orderId());

        try {
            Invoice invoice = invoiceTransactionBoundary.saveNewInvoice(invoiceService.buildInvoice(command));
            return handleInvoice(invoice);
        } catch (ConstraintViolationException e) {
            Invoice invoice = invoiceTransactionBoundary.findByOrderId(command.orderId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Invoice creation failed for orderId=" + command.orderId(), e
                    ));

            return handleInvoice(invoice);
        }
    }

    /**
     * Routes invoice processing based on the current local invoice state.
     *
     * @param invoice local invoice aggregate
     * @return local invoice identifier
     * @throws IllegalStateException if the invoice is not in a recoverable state
     */
    private InvoiceIssueResult handleInvoice(Invoice invoice) {
        if (invoice.isDuplicated()) {
            log.warn("Invoice duplicated in Fakturownia. Reconciliation required. invoiceId={}, orderId={}",
                    invoice.getId(), invoice.getOrderId());
            return new InvoiceIssueResult.Duplicated(invoice.getId());
        }

        if (invoice.isIssued()) {
            log.info("Invoice already issued. invoiceId={}, orderId={}", invoice.getId(), invoice.getOrderId());
            return new InvoiceIssueResult.Issued(invoice.getId());
        }

        if (invoice.isIssuing()) {
            return recoverOrIssue(invoice);
        }

        if (invoice.isDraft() || invoice.isIssueUnknown()) {
            invoiceTransactionBoundary.markAsIssuing(invoice);
            return recoverOrIssue(invoice);
        }

//        guard clause - should never happen
        throw new IllegalStateException(
                "Unsupported invoice state. invoiceId=" + invoice.getId() + ", orderId=" + invoice.getOrderId()
        );
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

    private InvoiceIssueResult recoverOrIssue(Invoice invoice) {
        log.info(">>> Trying to recover or issue invoice. invoiceId={}, orderId={}",
                invoice.getId(), invoice.getOrderId());

        String orderId = invoice.getOrderId().toString();

        try {
            return recoverIfExists(invoice, orderId)
                    .orElseGet(() -> issueWithRecovery(invoice, orderId));
        } catch (RuntimeException exception) {
            invoiceTransactionBoundary.markIssueUnknown(invoice);
            log.warn("!!! Invoice issue result unknown. invoiceId={}, orderId={}",
                    invoice.getId(), invoice.getOrderId(), exception);
            return new InvoiceIssueResult.PendingConfirmation(invoice.getId());
        }
    }

    private Optional<InvoiceIssueResult> reconcileFromExisting(
            Invoice localInvoice,
            List<FakturowniaGetInvoiceDto> externalInvoices
    ) {
        List<FakturowniaGetInvoiceDto> matchingInvoices = externalInvoices.stream()
                .filter(external -> isSameBusinessInvoice(localInvoice, external))
                .toList();

        if (matchingInvoices.size() == 1) {
            FakturowniaGetInvoiceDto externalInvoice = matchingInvoices.getFirst();
            invoiceTransactionBoundary.markInvoiceAsIssued(localInvoice, externalInvoice.id());

            log.info("<<< Invoice reconciled from Fakturownia. invoiceId={}, orderId={}, externalId={}",
                    localInvoice.getId(), localInvoice.getOrderId(), externalInvoice.id());

            return Optional.of(new InvoiceIssueResult.Issued(localInvoice.getId()));
        }

        if (matchingInvoices.size() > 1) {
            invoiceTransactionBoundary.markInvoiceAsDuplicated(localInvoice);

            log.warn("!!! Duplicate invoice detected in Fakturownia after reconciliation. invoiceId={}, orderId={}, matchingExternalIds={}",
                    localInvoice.getId(),
                    localInvoice.getOrderId(),
                    matchingInvoices.stream().map(FakturowniaGetInvoiceDto::id).toList());

            return Optional.of(new InvoiceIssueResult.Duplicated(localInvoice.getId()));
        }

        return Optional.empty();
    }

    private boolean isSameBusinessInvoice(Invoice localInvoice, FakturowniaGetInvoiceDto externalInvoice) {
        return equalsNormalized(localInvoice.getOrderId().toString(), externalInvoice.orderId());
    }

    private boolean equalsNormalized(String left, String right) {
        return Objects.equals(simpleNormalize(left), simpleNormalize(right));
    }

    private String simpleNormalize(String value) {
        if (value == null) {
            return null;
        }
        return value.trim().toUpperCase();
    }

    private Optional<InvoiceIssueResult> recoverIfExists(Invoice invoice, String orderId) {
        return reconcileFromExisting(invoice, taxSystemPort.findByOrderId(orderId));
    }

    private InvoiceIssueResult issueWithRecovery(Invoice invoice, String orderId) {
        try {
            String externalId = taxSystemPort.issueInvoice(invoice);
            invoiceTransactionBoundary.markInvoiceAsIssued(invoice, externalId);

            log.info("<<< Invoice issued. invoiceId={}, orderId={}, externalId={}",
                    invoice.getId(), invoice.getOrderId(), externalId);

            return new InvoiceIssueResult.Issued(invoice.getId());
        } catch (RuntimeException exception) {
            return recoverAfterIssueFailure(invoice, orderId, exception);
        }
    }

    private InvoiceIssueResult recoverAfterIssueFailure(
            Invoice invoice,
            String orderId,
            RuntimeException issueException
    ) {
        return reconcileFromExisting(invoice, taxSystemPort.findByOrderId(orderId))
                .orElseGet(() -> {
                    invoiceTransactionBoundary.markIssueUnknown(invoice);
                    log.warn("!!! Invoice visibility in fakturownia pending. invoiceId={}, orderId={}",
                            invoice.getId(), invoice.getOrderId(), issueException);
                    return new InvoiceIssueResult.PendingConfirmation(invoice.getId());
                });
    }

    /**
     * Resolves the local invoice using records already present in the external system.
     * <p>
     * If exactly one matching external invoice is found, the local invoice is marked as issued.
     * If multiple matching external invoices are found, the local invoice remains the source of truth
     * and remains the authoritative record for the order, even if the external system contains duplicates.
     * Local invoice is marked as duplicated so later reconciliation can remove or ignore external duplicates.
     *
     * @param localInvoice     local source-of-truth invoice
     * @param externalInvoices invoices found in the external system for the same order
     * @return local invoice identifier when the state can be resolved; otherwise empty
     */
    private Optional<InvoiceIssueResult> tryRecoverFromExisting(
            Invoice localInvoice,
            List<FakturowniaGetInvoiceDto> externalInvoices
    ) {
        if (externalInvoices.size() == 1) {
            FakturowniaGetInvoiceDto externalInvoice = externalInvoices.getFirst();
            invoiceTransactionBoundary.markInvoiceAsIssued(localInvoice, externalInvoice.id());

            log.info("<<< Invoice recovered from Fakturownia. invoiceId={}, orderId={}, externalId={}",
                    localInvoice.getId(), localInvoice.getOrderId(), externalInvoice.id());

            return Optional.of(new InvoiceIssueResult.Issued(localInvoice.getId()));
        }

        if (externalInvoices.size() > 1) {
            invoiceTransactionBoundary.markInvoiceAsDuplicated(localInvoice);

            log.warn("!!! Duplicate invoice detected in Fakturownia. Reconciliation required. invoiceId={}, orderId={}, matches={}",
                    localInvoice.getId(), localInvoice.getOrderId(), externalInvoices.size());

            return Optional.of(new InvoiceIssueResult.Duplicated(localInvoice.getId()));
        }

        return Optional.empty();
    }
}
