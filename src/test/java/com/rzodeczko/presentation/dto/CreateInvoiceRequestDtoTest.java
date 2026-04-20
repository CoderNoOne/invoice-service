package com.rzodeczko.presentation.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CreateInvoiceRequestDto.
 */
class CreateInvoiceRequestDtoTest {

    @Test
    void constructor_shouldCreateDto() {
        UUID orderId = UUID.randomUUID();
        String taxId = "123";
        String buyerName = "Buyer";
        List<CreateInvoiceRequestDto.ItemRequestDto> items = List.of(
                new CreateInvoiceRequestDto.ItemRequestDto("Item1", 1, BigDecimal.ONE)
        );
        CreateInvoiceRequestDto dto = new CreateInvoiceRequestDto(orderId, taxId, buyerName, items);
        assertEquals(orderId, dto.orderId());
        assertEquals(taxId, dto.taxId());
        assertEquals(buyerName, dto.buyerName());
        assertEquals(items, dto.items());
    }

    @Test
    void itemRequestDto_constructor_shouldCreateDto() {
        String name = "Item1";
        int quantity = 1;
        BigDecimal price = BigDecimal.ONE;
        CreateInvoiceRequestDto.ItemRequestDto item = new CreateInvoiceRequestDto.ItemRequestDto(name, quantity, price);
        assertEquals(name, item.name());
        assertEquals(quantity, item.quantity());
        assertEquals(price, item.price());
    }

    @Test
    void equals_shouldReturnTrueForSameValues() {
        UUID orderId = UUID.randomUUID();
        CreateInvoiceRequestDto dto1 = new CreateInvoiceRequestDto(orderId, "123", "Buyer", List.of());
        CreateInvoiceRequestDto dto2 = new CreateInvoiceRequestDto(orderId, "123", "Buyer", List.of());
        assertEquals(dto1, dto2);
    }

    @Test
    void hashCode_shouldBeSameForSameValues() {
        UUID orderId = UUID.randomUUID();
        CreateInvoiceRequestDto dto1 = new CreateInvoiceRequestDto(orderId, "123", "Buyer", List.of());
        CreateInvoiceRequestDto dto2 = new CreateInvoiceRequestDto(orderId, "123", "Buyer", List.of());
        assertEquals(dto1.hashCode(), dto2.hashCode());
    }
}
