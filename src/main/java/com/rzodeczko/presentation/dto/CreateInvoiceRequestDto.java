package com.rzodeczko.presentation.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateInvoiceRequestDto(
        @NotNull(message = "Order ID cannot be null")
        UUID orderId,

        @NotBlank(message = "Tax ID cannot be blank")
        String taxId,

        @NotBlank(message = "Buyer name cannot be blank")
        String buyerName,

        @NotNull(message = "Items list cannot be null")
        @NotEmpty(message = "Items list cannot be empty")
        @Valid
        List<ItemRequestDto> items

) {
    public record ItemRequestDto(
            @NotBlank(message = "Product name required")
            String name,

            @Min(value = 1, message = "Quantity must be at least 1")
            int quantity,

            @NotNull(message = "Price required")
            @DecimalMin(value = "0.01", message = "Price must be greater than 0")
            BigDecimal price,

            @DecimalMin(value = "0", message = "Tax rate cannot be negative")
            BigDecimal taxRate
    ) {
    }
}
