package com.nido.backend.money.infrastructure.web;

import com.nido.backend.money.application.port.in.CalculateRentRatioUseCase;
import com.nido.backend.money.domain.model.RentRatio;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/money")
public class RentRatioController {
    private final CalculateRentRatioUseCase calculateRentRatio;
    public RentRatioController(CalculateRentRatioUseCase calculateRentRatio) {
        this.calculateRentRatio = calculateRentRatio;
    }

    @PostMapping("/rent-ratio")
    public RentRatioResponse calculate(@RequestBody RentRatioRequest request) {
        RentRatio ratio = calculateRentRatio.calculate( request.netMonthlyIncome() , request.rentShare() );
        return RentRatioResponse.from(ratio);
    }
}
