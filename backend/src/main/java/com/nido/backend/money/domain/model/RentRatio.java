package com.nido.backend.money.domain.model;

import java.math.BigDecimal;
/**
 * Result of the rent-to-income calculation (spec 001).
 *
 * @param percentage share of the net monthly income taken by the rent, e.g. 30.0
 * @param level      traffic light for the rent-to-income ratio (spec 001, AC2)
 * @param rentExceedsIncome true if the rent exceeds the net monthly income (spec 001, AC3)
 */
public record RentRatio (BigDecimal percentage, EffortLevel level, boolean rentExceedsIncome){

}
