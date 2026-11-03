package com.nido.backend.money.infrastructure.web;

import com.nido.backend.money.domain.exception.InvalidAmountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Maps money-domain errors to RFC 9457 Problem Details (spec 001, AC5). Never echoes amounts. */
@RestControllerAdvice
class MoneyExceptionHandler {

    @ExceptionHandler(InvalidAmountException.class)
    ProblemDetail handleInvalidAmount(InvalidAmountException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problem.setTitle("Invalid amount");
        return problem;
    }
}