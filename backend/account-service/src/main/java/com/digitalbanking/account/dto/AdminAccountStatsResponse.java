package com.digitalbanking.account.dto;

import java.math.BigDecimal;

public class AdminAccountStatsResponse {

    private long totalCustomers;
    private long totalAccounts;
    private long activeAccounts;
    private BigDecimal totalBalance;

    public AdminAccountStatsResponse() {
    }

    public AdminAccountStatsResponse(
            long totalCustomers,
            long totalAccounts,
            long activeAccounts,
            BigDecimal totalBalance
    ) {
        this.totalCustomers = totalCustomers;
        this.totalAccounts = totalAccounts;
        this.activeAccounts = activeAccounts;
        this.totalBalance = totalBalance;
    }

    public long getTotalCustomers() {
        return totalCustomers;
    }

    public long getTotalAccounts() {
        return totalAccounts;
    }

    public long getActiveAccounts() {
        return activeAccounts;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }
}