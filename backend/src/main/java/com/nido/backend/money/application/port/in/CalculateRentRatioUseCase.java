package com.nido.backend.money.application.port.in;

import com.nido.backend.money.domain.model.RentRatio;

import java.math.BigDecimal;

/**
 * Input port: calculate the rent-to-income ratio (spec 001, US-2.3).
 */
public interface CalculateRentRatioUseCase {
    RentRatio calculate(BigDecimal netMonthlyIncome, BigDecimal rentShare);
}
