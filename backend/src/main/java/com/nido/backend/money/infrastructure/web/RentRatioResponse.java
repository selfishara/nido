package com.nido.backend.money.infrastructure.web;

import com.nido.backend.money.domain.model.RentRatio;

import java.math.BigDecimal;

/**
 * Outgoing JSON for POST /api/v1/money/rent-ratio. Public API contract, decoupled from the domain model.
 */
public record RentRatioResponse(BigDecimal percentage, String level, BigDecimal healthyMaxRent,
                                boolean rentExceedsIncome) {
    static RentRatioResponse from(RentRatio ratio) {
        return new RentRatioResponse(
                ratio.percentage(),
                ratio.level().name(),
                ratio.healthyMaxRent(),
                ratio.rentExceedsIncome()
        );
    }
}
