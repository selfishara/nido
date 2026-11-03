package com.nido.backend.money.infrastructure.web;

import java.math.BigDecimal;

/* incoming JSON body for POST /api/v1/money/rent-ratio.*/
public record RentRatioRequest(BigDecimal netMonthlyIncome, BigDecimal rentShare) {


}
