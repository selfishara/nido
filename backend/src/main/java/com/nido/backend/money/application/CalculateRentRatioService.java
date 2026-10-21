package com.nido.backend.money.application;

import com.nido.backend.money.application.port.in.CalculateRentRatioUseCase;
import com.nido.backend.money.domain.model.RentRatio;
import com.nido.backend.money.domain.service.RentRatioCalculator;
import java.math.BigDecimal;
/** Application service for US-2.3. Thin on purpose: rules live in the domain; this is where persistence or auth will plug in later. */


public class CalculateRentRatioService implements CalculateRentRatioUseCase {
    private final RentRatioCalculator rentRatioCalculator;

    public CalculateRentRatioService(RentRatioCalculator rentRatioCalculator) {
        this.rentRatioCalculator = rentRatioCalculator;
    }

    @Override
    public RentRatio calculate(java.math.BigDecimal netMonthlyIncome, BigDecimal rentShare) {
        return rentRatioCalculator.calculate(netMonthlyIncome, rentShare);
    }
}
