package com.digitalbanking.account.dto;

import java.math.BigDecimal;

public class FundAccountResponse {

    private String fundingReference;
    private Long accountId;
    private String accountNumber;
    private BigDecimal amount;
    private BigDecimal balance;
    private String currency;

    public FundAccountResponse() {
    }

    public FundAccountResponse(
            String fundingReference,
            Long accountId,
            String accountNumber,
            BigDecimal amount,
            BigDecimal balance,
            String currency
    ) {
        this.fundingReference = fundingReference;
        this.accountId = accountId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.balance = balance;
        this.currency = currency;
    }

    public String getFundingReference() {
        return fundingReference;
    }

    public Long getAccountId() {
        return accountId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }
}