package com.digitalbanking.transaction.exception;

public class AccountServiceBusinessException extends RuntimeException {

    public AccountServiceBusinessException(String message) {
        super(message);
    }
}