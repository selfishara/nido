package com.nido.backend.money.domain.model;

import java.math.BigDecimal;

/**
 * Thresholds of the traffic light (spec 001, AC2 + R3: system configuration, not user input).
 *
 * @param amberFrom ratio from which the level is AMBER (inclusive)
 * @param redAbove  ratio above which the level is RED (exclusive)
 */
public record EffortThresholds(BigDecimal amberFrom, BigDecimal redAbove) {
    public static final EffortThresholds DEFAULT =
            new EffortThresholds(new BigDecimal("30"), new BigDecimal("40"));

    public EffortLevel classify(BigDecimal percentage) {
        if (percentage.compareTo(amberFrom) < 0) {
            return EffortLevel.GREEN;
        }
        if (percentage.compareTo(redAbove) > 0) {
            return EffortLevel.RED;
        }
        return EffortLevel.AMBER;
    }
}
