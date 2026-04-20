package com.rzodeczko.infrastructure.persistence.entity;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for InvoiceItemEmbeddable.
 */
class InvoiceItemEmbeddableTest {

    @Test
    void builder_shouldCreateEmbeddable() {
        String name = "Item1";
        int quantity = 1;
        BigDecimal unitPrice = BigDecimal.ONE;

        InvoiceItemEmbeddable item = InvoiceItemEmbeddable.builder()
                .name(name)
                .quantity(quantity)
                .unitPrice(unitPrice)
                .build();

        assertEquals(name, item.getName());
        assertEquals(quantity, item.getQuantity());
        assertEquals(unitPrice, item.getUnitPrice());
    }

    @Test
    void noArgsConstructor_shouldCreateEmpty() {
        InvoiceItemEmbeddable item = new InvoiceItemEmbeddable();
        assertNull(item.getName());
        assertEquals(0, item.getQuantity());
        assertNull(item.getUnitPrice());
    }

    @Test
    void allArgsConstructor_shouldCreateWithValues() {
        String name = "Item1";
        int quantity = 1;
        BigDecimal unitPrice = BigDecimal.ONE;

        InvoiceItemEmbeddable item = new InvoiceItemEmbeddable(name, quantity, unitPrice);

        assertEquals(name, item.getName());
        assertEquals(quantity, item.getQuantity());
        assertEquals(unitPrice, item.getUnitPrice());
    }

    @Test
    void equals_shouldReturnTrueForSameValues() {
        InvoiceItemEmbeddable item1 = new InvoiceItemEmbeddable("Item1", 1, BigDecimal.ONE);
        InvoiceItemEmbeddable item2 = new InvoiceItemEmbeddable("Item1", 1, BigDecimal.ONE);
        assertEquals(item1, item2);
    }

    @Test
    void hashCode_shouldBeSameForSameValues() {
        InvoiceItemEmbeddable item1 = new InvoiceItemEmbeddable("Item1", 1, BigDecimal.ONE);
        InvoiceItemEmbeddable item2 = new InvoiceItemEmbeddable("Item1", 1, BigDecimal.ONE);
        assertEquals(item1.hashCode(), item2.hashCode());
    }
}
