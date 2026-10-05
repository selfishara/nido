package com.nido.backend.money.domain.service;

import com.nido.backend.money.domain.model.RentRatio;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Calculates how much of the net monthly income a rent takes (spec 001, rules R1–R2).
 * Pure domain logic: no framework dependencies.
 */
public class RentRatioCalculator {

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