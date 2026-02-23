package com.example.order_management.domain.valueobject;

import com.example.order_management.domain.exception.CurrencyMismatchException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Immutable Value Object representing monetary amounts.
 * Supports addition, subtraction, and multiplication.
 * All operations require same currency - throws CurrencyMismatchException otherwise.
 */
public final class Money {

    private static final String DEFAULT_CURRENCY = "USD";
    private static final int SCALE = 2;

    private final BigDecimal amount;
    private final String currency;

    public Money(BigDecimal amount) {
        this(amount, DEFAULT_CURRENCY);
    }

    public Money(BigDecimal amount, String currency) {
        if (amount == null) {
            throw new IllegalArgumentException("amount cannot be null");
        }
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("currency cannot be null or blank");
        }
        this.amount = amount.setScale(SCALE, RoundingMode.HALF_UP);
        this.currency = currency;
    }

    public static Money zero(String currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public Money add(Money other) {
        ensureSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        ensureSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    public Money multiply(int factor) {
        return multiply(BigDecimal.valueOf(factor));
    }

    public Money multiply(BigDecimal factor) {
        if (factor == null) {
            throw new IllegalArgumentException("factor cannot be null");
        }
        return new Money(this.amount.multiply(factor), this.currency);
    }

    public boolean isLessThan(Money other) {
        ensureSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    private void ensureSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new CurrencyMismatchException(
                    "Currency mismatch: cannot operate on " + this.currency + " and " + other.currency);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 && Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}
