package com.rzodeczko.infrastructure.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItemEmbeddable {
    private String name;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal taxRate;
}
