package com.nido.backend.money.infrastructure.config;

import com.nido.backend.money.application.CalculateRentRatioService;
import com.nido.backend.money.application.port.in.CalculateRentRatioUseCase;
import com.nido.backend.money.domain.service.RentRatioCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Wires the money module: domain and application stay framework-free (ADR-0002). */
@Configuration
public class MoneyConfig {

    @Bean
    RentRatioCalculator rentRatioCalculator() {
        return new RentRatioCalculator();
    }

    @Bean
    CalculateRentRatioUseCase calculateRentRatioUseCase(RentRatioCalculator calculator) {
        return new CalculateRentRatioService(calculator);
    }
}
