package com.rzodeczko.presentation.dto;

import java.util.UUID;

public record CreateInvoiceResponseDto(
        UUID invoiceId,
        String status,
        String message
) {
}