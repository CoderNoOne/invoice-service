package com.rzodeczko.infrastructure.fakturownia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record InvoiceDto(
        String kind,
        @JsonProperty("sell_date") String sellDate,
        @JsonProperty("issue_date") String issueDate,
        @JsonProperty("payment_to") String paymentTo,
        @JsonProperty("buyer_name") String buyerName,
        @JsonProperty("buyer_tax_no") String buyerTaxNo,
        List<PositionDto> positions
) {
}
