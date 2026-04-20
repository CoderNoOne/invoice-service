package com.rzodeczko.infrastructure.usecase;

import com.rzodeczko.application.exception.ExternalTaxSystemException;
import com.rzodeczko.application.port.output.TaxSystemPort;
import com.rzodeczko.domain.model.Invoice;
import com.rzodeczko.infrastructure.transaction.InvoiceTransactionBoundary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Unit tests for InvoiceWebhookUseCaseImpl.
 */
class InvoiceWebhookUseCaseImplTest {
    private InvoiceTransactionBoundary invoiceTransactionBoundary;
    private TaxSystemPort taxSystemPort;
    private InvoiceWebhookUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        invoiceTransactionBoundary = mock(InvoiceTransactionBoundary.class);
        taxSystemPort = mock(TaxSystemPort.class);
        useCase = new InvoiceWebhookUseCaseImpl(invoiceTransactionBoundary, taxSystemPort);
    }

    @Test
    void handle_shouldDoNothingIfInvoiceNotFound() {
        String externalId = "ext-1";
        when(invoiceTransactionBoundary.findByExternalId(externalId)).thenReturn(Optional.empty());

        useCase.handle(externalId);

        verify(invoiceTransactionBoundary).findByExternalId(externalId);
        verifyNoMoreInteractions(invoiceTransactionBoundary, taxSystemPort);
    }

    @Test
    void handle_shouldDoNothingIfPdfContentNotFound() {
        String externalId = "ext-1";
        Invoice invoice = mock(Invoice.class);
        UUID invoiceId = UUID.randomUUID();
        when(invoice.getId()).thenReturn(invoiceId);
        when(invoiceTransactionBoundary.findByExternalId(externalId)).thenReturn(Optional.of(invoice));
        when(invoiceTransactionBoundary.findPdfContent(invoiceId)).thenReturn(Optional.empty());

        useCase.handle(externalId);

        verify(invoiceTransactionBoundary).findByExternalId(externalId);
        verify(invoiceTransactionBoundary).findPdfContent(invoiceId);
        verifyNoMoreInteractions(invoiceTransactionBoundary, taxSystemPort);
    }

    @Test
    void handle_shouldDoNothingIfTaxSystemThrowsException() {
        String externalId = "ext-1";
        Invoice invoice = mock(Invoice.class);
        UUID invoiceId = UUID.randomUUID();
        when(invoice.getId()).thenReturn(invoiceId);
        when(invoiceTransactionBoundary.findByExternalId(externalId)).thenReturn(Optional.of(invoice));
        when(invoiceTransactionBoundary.findPdfContent(invoiceId)).thenReturn(Optional.of(new byte[]{1}));
        when(taxSystemPort.getPdf(externalId)).thenThrow(new ExternalTaxSystemException("error"));

        useCase.handle(externalId);

        verify(invoiceTransactionBoundary).findByExternalId(externalId);
        verify(invoiceTransactionBoundary).findPdfContent(invoiceId);
        verify(taxSystemPort).getPdf(externalId);
        verifyNoMoreInteractions(invoiceTransactionBoundary, taxSystemPort);
    }

    @Test
    void handle_shouldDoNothingIfPdfIsNull() {
        String externalId = "ext-1";
        Invoice invoice = mock(Invoice.class);
        UUID invoiceId = UUID.randomUUID();
        when(invoice.getId()).thenReturn(invoiceId);
        when(invoiceTransactionBoundary.findByExternalId(externalId)).thenReturn(Optional.of(invoice));
        when(invoiceTransactionBoundary.findPdfContent(invoiceId)).thenReturn(Optional.of(new byte[]{1}));
        when(taxSystemPort.getPdf(externalId)).thenReturn(null);

        useCase.handle(externalId);

        verify(invoiceTransactionBoundary).findByExternalId(externalId);
        verify(invoiceTransactionBoundary).findPdfContent(invoiceId);
        verify(taxSystemPort).getPdf(externalId);
        verifyNoMoreInteractions(invoiceTransactionBoundary, taxSystemPort);
    }

    @Test
    void handle_shouldDoNothingIfPdfIsEmpty() {
        String externalId = "ext-1";
        Invoice invoice = mock(Invoice.class);
        UUID invoiceId = UUID.randomUUID();
        when(invoice.getId()).thenReturn(invoiceId);
        when(invoiceTransactionBoundary.findByExternalId(externalId)).thenReturn(Optional.of(invoice));
        when(invoiceTransactionBoundary.findPdfContent(invoiceId)).thenReturn(Optional.of(new byte[]{1}));
        when(taxSystemPort.getPdf(externalId)).thenReturn(new byte[0]);

        useCase.handle(externalId);

        verify(invoiceTransactionBoundary).findByExternalId(externalId);
        verify(invoiceTransactionBoundary).findPdfContent(invoiceId);
        verify(taxSystemPort).getPdf(externalId);
        verifyNoMoreInteractions(invoiceTransactionBoundary, taxSystemPort);
    }

    @Test
    void handle_shouldSavePdfContentIfValid() {
        String externalId = "ext-1";
        Invoice invoice = mock(Invoice.class);
        UUID invoiceId = UUID.randomUUID();
        when(invoice.getId()).thenReturn(invoiceId);
        when(invoiceTransactionBoundary.findByExternalId(externalId)).thenReturn(Optional.of(invoice));
        when(invoiceTransactionBoundary.findPdfContent(invoiceId)).thenReturn(Optional.of(new byte[]{1}));
        byte[] freshPdf = new byte[]{2, 3, 4};
        when(taxSystemPort.getPdf(externalId)).thenReturn(freshPdf);

        useCase.handle(externalId);

        verify(invoiceTransactionBoundary).findByExternalId(externalId);
        verify(invoiceTransactionBoundary).findPdfContent(invoiceId);
        verify(taxSystemPort).getPdf(externalId);
        verify(invoiceTransactionBoundary).savePdfContent(invoiceId, freshPdf);
        verifyNoMoreInteractions(invoiceTransactionBoundary, taxSystemPort);
    }
}
