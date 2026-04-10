package com.rzodeczko.infrastructure.fakturownia.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public record PositionDto(
        String name,
        int tax,
        int quantity,
        @JsonProperty("total_price_gross") BigDecimal totalPriceGross
) {
}
