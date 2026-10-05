package com.nido.backend.money.domain.service;

import com.nido.backend.money.domain.model.RentRatio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RentRatioCalculatorTest {

    private final RentRatioCalculator calculator = new RentRatioCalculator();

    @Test
    void ac1_calculatesRatioAsPercentageOfNetIncome() {
        RentRatio result = calculator.calculate(new BigDecimal("1500.00"), new BigDecimal("450.00"));

        assertThat(result.percentage()).isEqualByComparingTo("30.0");
    }
}