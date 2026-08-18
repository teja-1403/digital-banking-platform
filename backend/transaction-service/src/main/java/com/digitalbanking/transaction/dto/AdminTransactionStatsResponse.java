package com.digitalbanking.transaction.dto;

import java.math.BigDecimal;

public class AdminTransactionStatsResponse {

    private long totalTransactions;
    private long completedTransactions;
    private long failedTransactions;
    private BigDecimal totalTransactionVolume;

    public AdminTransactionStatsResponse() {
    }

    public AdminTransactionStatsResponse(
            long totalTransactions,
            long completedTransactions,
            long failedTransactions,
            BigDecimal totalTransactionVolume
    ) {
        this.totalTransactions = totalTransactions;
        this.completedTransactions =
                completedTransactions;
        this.failedTransactions =
                failedTransactions;
        this.totalTransactionVolume =
                totalTransactionVolume;
    }

    public long getTotalTransactions() {
        return totalTransactions;
    }

    public long getCompletedTransactions() {
        return completedTransactions;
    }

    public long getFailedTransactions() {
        return failedTransactions;
    }

    public BigDecimal getTotalTransactionVolume() {
        return totalTransactionVolume;
    }
}