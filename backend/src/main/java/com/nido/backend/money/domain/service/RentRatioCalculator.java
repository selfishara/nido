package com.nido.backend.money.domain.service;

import com.nido.backend.money.domain.model.EffortLevel;
import com.nido.backend.money.domain.model.EffortThresholds;
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
    private final EffortThresholds thresholds = EffortThresholds.DEFAULT;

    public RentRatio calculate(BigDecimal netMonthlyIncome, BigDecimal rentShare) {
        // Multiply first, then divide once: a single rounding step.
        // Dividing first and rounding twice can turn 29.949…% into 30.0% (wrong traffic light).

        BigDecimal percentage = rentShare.multiply(ONE_HUNDRED).divide(netMonthlyIncome, PERCENTAGE_SCALE, RoundingMode.HALF_UP);

        EffortLevel level = EffortThresholds.DEFAULT.classify(percentage);

        boolean rentExceedsIncome = rentShare.compareTo(netMonthlyIncome) > 0;

        BigDecimal healthyMaxRent = healthyMaxRent(netMonthlyIncome);

        return new RentRatio(percentage, level, rentExceedsIncome, healthyMaxRent);

    }

    // R4: start from the amber threshold and step down 1 € until the rounded ratio is GREEN.
    // Reuses percentageOf + classify, so it always agrees with what the user sees (R2).
    private BigDecimal healthyMaxRent(BigDecimal netMonthlyIncome) {
        BigDecimal candidate = netMonthlyIncome
                .multiply(thresholds.amberFrom())
                .divide(ONE_HUNDRED, 0, RoundingMode.FLOOR);

        while (thresholds.classify(percentageOf(netMonthlyIncome, candidate)) != EffortLevel.GREEN) {
            candidate = candidate.subtract(BigDecimal.ONE);
        }
        return candidate;
    }

    // Multiply first, then divide once: a single rounding step.
    // Dividing first and rounding twice can turn 29.949…% into 30.0% (wrong traffic light).
    private BigDecimal percentageOf(BigDecimal netMonthlyIncome, BigDecimal rentShare) {
        return rentShare
                .multiply(ONE_HUNDRED)
                .divide(netMonthlyIncome, PERCENTAGE_SCALE, RoundingMode.HALF_UP);
    }
}