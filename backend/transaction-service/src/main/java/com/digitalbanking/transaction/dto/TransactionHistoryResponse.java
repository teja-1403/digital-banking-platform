package com.digitalbanking.transaction.dto;

import com.digitalbanking.transaction.entity.TransactionStatus;
import com.digitalbanking.transaction.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionHistoryResponse {

    private String transactionReference;
    private TransactionType type;
    private TransactionStatus status;
    private Long sourceAccountId;
    private Long destinationAccountId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public TransactionHistoryResponse() {
    }

    public TransactionHistoryResponse(
            String transactionReference,
            TransactionType type,
            TransactionStatus status,
            Long sourceAccountId,
            Long destinationAccountId,
            BigDecimal amount,
            String currency,
            String description,
            LocalDateTime createdAt,
            LocalDateTime completedAt
    ) {
        this.transactionReference = transactionReference;
        this.type = type;
        this.status = status;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public TransactionType getType() {
        return type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public Long getDestinationAccountId() {
        return destinationAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }
}