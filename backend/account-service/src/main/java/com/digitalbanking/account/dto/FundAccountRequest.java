package com.digitalbanking.account.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class FundAccountRequest {

    @NotNull(message = "Funding amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Funding amount must be greater than zero"
    )
    @Digits(
            integer = 17,
            fraction = 2,
            message = "Funding amount cannot have more than 2 decimal places"
    )
    private BigDecimal amount;

    public FundAccountRequest() {
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}