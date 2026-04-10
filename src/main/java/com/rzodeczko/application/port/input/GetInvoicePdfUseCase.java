package com.rzodeczko.application.port.input;

import java.util.UUID;

public interface GetInvoicePdfUseCase {
    byte[] getPdf(UUID invoiceId);
}
