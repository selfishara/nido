package com.nido.backend.money.domain.service;

import com.nido.backend.money.domain.exception.InvalidAmountException;
import com.nido.backend.money.domain.model.EffortLevel;
import com.nido.backend.money.domain.model.EffortThresholds;
import com.nido.backend.money.domain.model.RentRatio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @ParameterizedTest(name = "{0} / {1} -> {2}% {3}")
    @CsvSource({
            "1500.00, 449.40, 30.0, AMBER",
            "10000.00, 2994.96, 29.9, GREEN"
    })
    void r2_classifiesTheRoundedPercentage(String income, String rent, String expectedPercentage, EffortLevel expectedLevel){
        RentRatio result = calculator.calculate(new BigDecimal(income), new BigDecimal(rent));

        assertThat(result.percentage()).isEqualByComparingTo(expectedPercentage);
        assertThat(result.level()).isEqualTo(expectedLevel);
    }

    @ParameterizedTest(name = "income {0} -> healthy max rent {1}")
    @CsvSource({
            "1500.00, 449",
            "1000.00, 299",
            "2000.00, 598" // trap: 599 / 2000 = 29.95% -> rounds to 30.0 -> AMBER
    })
    void ac4_returnsHealthyMaxRent(String income, String expectedMaxRent){
        RentRatio result = calculator.calculate(
                new BigDecimal(income),new BigDecimal("500.00"));

        assertThat(result.healthyMaxRent()).isEqualByComparingTo(expectedMaxRent);
    }

    @ParameterizedTest(name = "income {0}, rent {1} -> rejected")
    @CsvSource(nullValues = "null", value = {
            "null, 450.00",          // missing income
            "1500.00, null",         // missing rent
            "0, 450.00",             // zero income (would divide by zero!)
            "1500.00, 0",            // zero rent
            "-1500.00, 450.00",      // negative income
            "1500.00, -450.00",      // negative rent
            "1500.555, 450.00",      // 3 decimals
            "1500.00, 450.555",      // 3 decimals
            "1000000.01, 450.00",    // above the maximum
            "1500.00, 1000000.01"    // above the maximum
    })
    void ac5_rejectsInvalidAmounts(String income, String rent){
        assertThatThrownBy(() -> calculator.calculate(toAmount(income), toAmount(rent)))
                .isInstanceOf(InvalidAmountException.class);
    }

    @Test
    void ac5_acceptsTheMaximumAmount() {
        RentRatio result = calculator.calculate(new BigDecimal("1000000.00"), new BigDecimal("1000000.00"));

        assertThat(result.percentage()).isEqualByComparingTo("100.0");
    }

    private static BigDecimal toAmount(String value) {
        return value == null ? null : new BigDecimal(value);
    }
}