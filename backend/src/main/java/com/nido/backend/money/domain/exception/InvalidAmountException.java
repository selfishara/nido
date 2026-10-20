package com.nido.backend.money.domain.exception;
/** An amount breaks a domain rule (spec 001, AC5). Mapped to HTTP 400 at the web edge (T9). */
public class InvalidAmountException extends RuntimeException{
    public InvalidAmountException(String message){
        super(message);
    }
}
