package com.digitalbanking.transaction.repository;

import com.digitalbanking.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionReference(
            String transactionReference
    );

    Optional<Transaction> findByIdempotencyKey(
            String idempotencyKey
    );

    List<Transaction> findBySourceAccountIdOrderByCreatedAtDesc(
            Long sourceAccountId
    );

    List<Transaction> findByDestinationAccountIdOrderByCreatedAtDesc(
            Long destinationAccountId
    );

    List<Transaction> findBySourceAccountIdOrDestinationAccountIdOrderByCreatedAtDesc(
            Long sourceAccountId,
            Long destinationAccountId
    );
}