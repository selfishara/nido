package com.nido.backend.money.domain.service;

import com.nido.backend.money.domain.model.EffortLevel;
import com.nido.backend.money.domain.model.EffortThresholds;
import com.nido.backend.money.domain.model.RentRatio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class RentRatioCalculatorTest {

    private final RentRatioCalculator calculator = new RentRatioCalculator();

    @Test
    void ac1_calculatesRatioAsPercentageOfNetIncome() {
        RentRatio result = calculator.calculate(new BigDecimal("1500.00"), new BigDecimal("450.00"));

        assertThat(result.percentage()).isEqualByComparingTo("30.0");
    }

    @ParameterizedTest(name = "{0}% -> {1}")
    @CsvSource({
            "29.9, GREEN",
            "30.0, AMBER",
            "40.0, AMBER",
            "40.1, RED"
    })
    void ac2_classifiesPercentageIntoTrafficLight(String percentage, EffortLevel expected) {
        assertThat(EffortThresholds.DEFAULT.classify(new BigDecimal(percentage))).isEqualTo(expected);
    }


    @Test
    void ac3_rentAboveIncome_isRedWithFlag(){
        RentRatio result = calculator.calculate(new BigDecimal("1000.00"), new BigDecimal("1500.00"));

        assertThat(result.level()).isEqualTo(EffortLevel.RED);
        assertThat(result.rentExceedsIncome()).isTrue();
        assertThat(result.percentage()).isEqualByComparingTo("150.0");
    }
}