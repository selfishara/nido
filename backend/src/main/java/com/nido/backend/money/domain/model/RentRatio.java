package com.nido.backend.money.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Result of the rent-to-income calculation (spec 001).
 *
 * @param percentage share of the net monthly income taken by the rent, e.g. 30.0
 */
public record RentRatio (BigDecimal percentage){
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    private static final int PERCENTAGE_SCALE = 1;
    public RentRatio calculate(BigDecimal netMonthlyIncome, BigDecimal rentShare) {
        // Multiply first, then divide once: a single rounding step.
        // Dividing first and rounding twice can turn 29.949…% into 30.0% (wrong traffic light).
        BigDecimal percentage = rentShare
                .multiply(ONE_HUNDRED)
                .divide(netMonthlyIncome, PERCENTAGE_SCALE, RoundingMode.HALF_UP);

        return new RentRatio(percentage);
    }
}
