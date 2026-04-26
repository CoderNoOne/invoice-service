package com.rzodeczko.infrastructure.fakturownia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CreateInvoiceDto(
        String kind,
        @JsonProperty("sell_date") String sellDate,
        @JsonProperty("issue_date") String issueDate,
        @JsonProperty("payment_to") String paymentTo,
        @JsonProperty("buyer_name") String buyerName,
        @JsonProperty("buyer_tax_no") String buyerTaxNo,
        @JsonProperty("oid") String orderId,
        List<PositionDto> positions
) {
}
