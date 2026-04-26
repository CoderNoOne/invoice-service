package com.rzodeczko.infrastructure.fakturownia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreateInvoiceWrapperDto(
        @JsonProperty("invoice") CreateInvoiceDto invoice
) {
}
