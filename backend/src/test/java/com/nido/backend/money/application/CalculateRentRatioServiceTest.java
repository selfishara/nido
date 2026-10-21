package com.nido.backend.money.application;

import com.nido.backend.money.application.port.in.CalculateRentRatioUseCase;
import com.nido.backend.money.domain.exception.InvalidAmountException;
import com.nido.backend.money.domain.model.EffortLevel;
import com.nido.backend.money.domain.model.RentRatio;
import com.nido.backend.money.domain.service.RentRatioCalculator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CalculateRentRatioServiceTest {
    private final CalculateRentRatioUseCase useCase = new CalculateRentRatioService(new RentRatioCalculator());

@Test
void delegatesToTheDomainCalculator(){
    RentRatio result = useCase.calculate(new BigDecimal("1500.00"), new BigDecimal("450.00"));

    assertThat(result.percentage()).isEqualByComparingTo("30.0");
    assertThat(result.level()).isEqualTo(EffortLevel.AMBER);
}

@Test
    void propagatesDomainValidationErrors(){
    assertThatThrownBy(() -> useCase.calculate(null, new BigDecimal("450.00")))
            .isInstanceOf(InvalidAmountException.class);
}
}
