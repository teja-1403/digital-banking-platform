package com.digitalbanking.account.dto;

import com.digitalbanking.account.entity.AccountStatus;
import com.digitalbanking.account.entity.AccountType;

import java.math.BigDecimal;

public class AccountResponse {

    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal balance;
    private String currency;
    private AccountStatus status;

    public AccountResponse() {
    }

    public AccountResponse(
            Long id,
            String accountNumber,
            AccountType accountType,
            BigDecimal balance,
            String currency,
            AccountStatus status
    ) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.balance = balance;
        this.currency = currency;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }

    public AccountStatus getStatus() {
        return status;
    }
}