package com.nido.backend.money.domain.service;

import com.nido.backend.money.domain.exception.InvalidAmountException;
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
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("1000000");
    private static final int MAX_DECIMALS = 2;

    private final EffortThresholds thresholds = EffortThresholds.DEFAULT;

    public RentRatio calculate(BigDecimal netMonthlyIncome, BigDecimal rentShare) {

        //guard clausules
        requireValidAmount(netMonthlyIncome, "netMonthlyIncome");
        requireValidAmount(rentShare, "rentShare");

        // Multiply first, then divide once: a single rounding step.
        // Dividing first and rounding twice can turn 29.949…% into 30.0% (wrong traffic light).

        BigDecimal percentage = percentageOf(netMonthlyIncome, rentShare);
        EffortLevel level = thresholds.classify(percentage);
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

    /** Domain invariants for any amount (spec 001, AC5). Messages name the field, never the value. */
    private static void requireValidAmount(BigDecimal amount, String field) {
        if (amount == null) {
            throw new InvalidAmountException(field + " is required");
        }
        if (amount.signum() <= 0) {
            throw new InvalidAmountException(field + " must be greater than 0");
        }
        if (amount.stripTrailingZeros().scale() > MAX_DECIMALS) {
            throw new InvalidAmountException(field + " must have at most 2 decimals");
        }
        if (amount.compareTo(MAX_AMOUNT) > 0) {
            throw new InvalidAmountException(field + " must not exceed " + MAX_AMOUNT.toPlainString());
        }
    }
}