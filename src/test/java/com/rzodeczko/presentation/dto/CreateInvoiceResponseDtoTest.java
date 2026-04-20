package com.rzodeczko.presentation.dto;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CreateInvoiceResponseDto.
 */
class CreateInvoiceResponseDtoTest {

    @Test
    void constructor_shouldCreateDto() {
        UUID invoiceId = UUID.randomUUID();
        CreateInvoiceResponseDto dto = new CreateInvoiceResponseDto(invoiceId);
        assertEquals(invoiceId, dto.invoiceId());
    }

    @Test
    void equals_shouldReturnTrueForSameValues() {
        UUID invoiceId = UUID.randomUUID();
        CreateInvoiceResponseDto dto1 = new CreateInvoiceResponseDto(invoiceId);
        CreateInvoiceResponseDto dto2 = new CreateInvoiceResponseDto(invoiceId);
        assertEquals(dto1, dto2);
    }

    @Test
    void hashCode_shouldBeSameForSameValues() {
        UUID invoiceId = UUID.randomUUID();
        CreateInvoiceResponseDto dto1 = new CreateInvoiceResponseDto(invoiceId);
        CreateInvoiceResponseDto dto2 = new CreateInvoiceResponseDto(invoiceId);
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
