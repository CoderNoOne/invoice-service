package com.rzodeczko.domain.model;

import com.rzodeczko.domain.vo.TaxRate;

import java.math.BigDecimal;

/**
 * Entity representing an item on an invoice.
 */
public record InvoiceItem(
        String name,
        int quantity,
        BigDecimal unitPrice,
        TaxRate taxRate) {
    public InvoiceItem {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }

        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit price must be positive");
        }

        if (taxRate == null) {
            throw new IllegalArgumentException("Tax rate must not be null");
        }
    }

    public InvoiceItem(String name, int quantity, BigDecimal unitPrice) {
        this(name, quantity, unitPrice, TaxRate.of(23));
    }
}
