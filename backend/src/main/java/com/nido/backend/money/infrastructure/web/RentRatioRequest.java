package com.nido.backend.money.infrastructure.web;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * Incoming JSON body for POST /api/v1/money/rent-ratio.
 * Edge validation reports every invalid field at once; the domain re-checks the same rules (defense in depth).
 */
public record RentRatioRequest(
        @NotNull(message = "is required")
        @Positive(message = "must be greater than 0")
        @Digits(integer = 7, fraction = 2, message = "must have at most 2 decimals")
        @DecimalMax(value = "1000000", message = "must not exceed 1000000")
        BigDecimal netMonthlyIncome,
        @NotNull(message = "is required")
        @Positive(message = "must be greater than 0")
        @Digits(integer = 7, fraction = 2, message = "must have at most 2 decimals")
        @DecimalMax(value = "1000000", message = "must not exceed 1000000")
        BigDecimal rentShare) {
}
