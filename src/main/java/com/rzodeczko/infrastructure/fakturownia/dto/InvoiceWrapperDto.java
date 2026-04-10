package com.rzodeczko.infrastructure.fakturownia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record InvoiceWrapperDto(
        @JsonProperty("invoice") InvoiceDto invoice
) {
}
