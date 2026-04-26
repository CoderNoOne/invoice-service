package com.rzodeczko.application.port.output;


import com.rzodeczko.domain.model.Invoice;
import com.rzodeczko.presentation.dto.FakturowniaGetInvoiceDto;

import java.util.List;

public interface TaxSystemPort {
    String issueInvoice(Invoice invoice);

    byte[] getPdf(String externalId);

    List<FakturowniaGetInvoiceDto> findByOrderId(String orderId);
}
