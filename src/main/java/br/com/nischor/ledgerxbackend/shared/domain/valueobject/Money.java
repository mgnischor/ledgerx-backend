package br.com.nischor.ledgerxbackend.shared.domain.valueobject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * Immutable value object representing a monetary amount in a specific currency.
 *
 * <p>The amount is always normalized to the currency's default fraction digits
 * using {@link RoundingMode#HALF_EVEN} rounding, and arithmetic operations
 * enforce that both operands share the same currency.
 *
 * @param amount   the monetary amount, normalized to the currency's default scale
 * @param currency the currency the amount is expressed in
 */
public record Money(BigDecimal amount, Currency currency) implements Serializable {

    /**
     * Validates and normalizes the components of this {@code Money} instance.
     *
     * @throws IllegalArgumentException if {@code amount} or {@code currency} is {@code null}
     */
    public Money {
        if (amount == null) {
            throw new IllegalArgumentException("Amount must not be null");
        }
        if (currency == null) {
            throw new IllegalArgumentException("Currency must not be null");
        }
        amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_EVEN);
    }

    /**
     * Creates a {@code Money} instance in Brazilian Real (BRL).
     *
     * @param amount the monetary amount
     * @return a new {@code Money} instance with currency BRL
     */
    public static Money brl(BigDecimal amount) {
        return new Money(amount, Currency.getInstance("BRL"));
    }

    /**
     * Creates a zero-valued {@code Money} instance in the given currency.
     *
     * @param currency the currency for the zero amount
     * @return a new {@code Money} instance with amount zero
     */
    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    /**
     * Adds another {@code Money} value to this one.
     *
     * @param other the amount to add, must be in the same currency
     * @return a new {@code Money} instance representing the sum
     * @throws IllegalArgumentException if {@code other} has a different currency
     */
    public Money add(Money other) {
        requireSameCurrency(other);
        return new Money(amount.add(other.amount), currency);
    }

    /**
     * Subtracts another {@code Money} value from this one.
     *
     * @param other the amount to subtract, must be in the same currency
     * @return a new {@code Money} instance representing the difference
     * @throws IllegalArgumentException if {@code other} has a different currency
     */
    public Money subtract(Money other) {
        requireSameCurrency(other);
        return new Money(amount.subtract(other.amount), currency);
    }

    /**
     * Checks whether this amount is strictly negative.
     *
     * @return {@code true} if the amount is less than zero, {@code false} otherwise
     */
    public boolean isNegative() {
        return amount.signum() < 0;
    }

    /**
     * Checks whether this amount is strictly positive.
     *
     * @return {@code true} if the amount is greater than zero, {@code false} otherwise
     */
    public boolean isPositive() {
        return amount.signum() > 0;
    }

    /**
     * Ensures that the given {@code Money} instance uses the same currency as this one.
     *
     * @param other the amount to compare currencies with
     * @throws IllegalArgumentException if the currencies differ
     */
    private void requireSameCurrency(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot operate on different currencies: %s vs %s"
                    .formatted(currency, other.currency));
        }
    }
}
