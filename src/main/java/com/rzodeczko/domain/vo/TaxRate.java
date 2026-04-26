package com.rzodeczko.domain.vo;

import java.math.BigDecimal;

/**
 * Value object representing a tax rate.
 */
public record TaxRate(BigDecimal value) {
    public TaxRate {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tax rate must not be negative");
        }
    }

    public static TaxRate of(double value) {
        return new TaxRate(new BigDecimal(String.valueOf(value)));
    }

    public static TaxRate of(BigDecimal value) {
        return new TaxRate(value);
    }

    public int intValue() {
        return value.intValue();
    }
}
