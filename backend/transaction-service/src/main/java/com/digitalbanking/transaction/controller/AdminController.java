package com.digitalbanking.transaction.controller;

import com.digitalbanking.transaction.dto.AdminTransactionStatsResponse;
import com.digitalbanking.transaction.entity.TransactionStatus;
import com.digitalbanking.transaction.repository.TransactionRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final TransactionRepository transactionRepository;

    public AdminController(
            TransactionRepository transactionRepository
    ) {
        this.transactionRepository =
                transactionRepository;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/transaction-stats")
    public AdminTransactionStatsResponse
    getTransactionStats() {

        long totalTransactions =
                transactionRepository.count();

        long completedTransactions =
                transactionRepository.countByStatus(
                        TransactionStatus.COMPLETED
                );

        long failedTransactions =
                transactionRepository.countByStatus(
                        TransactionStatus.FAILED
                );

        return new AdminTransactionStatsResponse(
                totalTransactions,
                completedTransactions,
                failedTransactions,
                transactionRepository
                        .getCompletedTransactionVolume()
        );
    }
}