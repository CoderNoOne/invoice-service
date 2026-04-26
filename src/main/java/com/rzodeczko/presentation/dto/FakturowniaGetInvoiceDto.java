package com.rzodeczko.presentation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FakturowniaGetInvoiceDto(
        String id,
        String status,
        @JsonProperty("oid") String orderId
) {
}
