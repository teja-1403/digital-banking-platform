package com.digitalbanking.transaction.service;

import com.digitalbanking.transaction.client.AccountServiceClient;
import com.digitalbanking.transaction.dto.TransactionHistoryResponse;
import com.digitalbanking.transaction.dto.TransactionResponse;
import com.digitalbanking.transaction.dto.TransferRequest;
import com.digitalbanking.transaction.entity.Transaction;
import com.digitalbanking.transaction.entity.TransactionStatus;
import com.digitalbanking.transaction.entity.TransactionType;
import com.digitalbanking.transaction.exception.BusinessRuleException;
import com.digitalbanking.transaction.repository.TransactionRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digitalbanking.transaction.exception.AccountServiceBusinessException;
import com.digitalbanking.transaction.exception.AccountServiceUnavailableException;
import com.digitalbanking.transaction.exception.TransactionProcessingException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountServiceClient accountServiceClient;
    private final TransactionCreationService transactionCreationService;
    private final AuditLogService auditLogService;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountServiceClient accountServiceClient,
            TransactionCreationService transactionCreationService,
            AuditLogService auditLogService
    ) {
        this.transactionRepository = transactionRepository;
        this.accountServiceClient = accountServiceClient;
        this.transactionCreationService = transactionCreationService;
        this.auditLogService = auditLogService;
    }

    public TransactionResponse initiateTransfer(
            Long userId,
            String idempotencyKey,
            TransferRequest request
    ) {

        validateTransferRequest(request);

        if (idempotencyKey == null ||
                idempotencyKey.isBlank()) {

            throw new BusinessRuleException(
                    "Idempotency-Key header is required"
            );
        }

        /*
         * Fast path:
         * If this idempotency key already exists,
         * return the existing logical transaction.
         */
        var existingTransaction =
                transactionRepository.findByIdempotencyKey(
                        idempotencyKey
                );

        if (existingTransaction.isPresent()) {
            return toResponse(existingTransaction.get());
        }

        Transaction transaction;

        try {

            /*
             * Create the PENDING transaction in its own transaction.
             * The database unique constraint protects against
             * concurrent requests using the same idempotency key.
             */
            transaction =
                    transactionCreationService
                            .createPendingTransaction(
                                    idempotencyKey,
                                    request
                            );

        } catch (DataIntegrityViolationException ex) {

            /*
             * Another concurrent request won the race and
             * inserted this idempotency key first.
             */
            transaction =
                    transactionRepository
                            .findByIdempotencyKey(idempotencyKey)
                            .orElseThrow(() ->
                                    new BusinessRuleException(
                                            "Unable to resolve idempotent transaction"
                                    )
                            );

            return toResponse(transaction);
        }

        /*
         * Audit: transfer initiated
         */
        auditLogService.log(
                userId,
                "TRANSFER_INITIATED",
                transaction.getTransactionReference(),
                "PENDING",
                "Transfer initiated"
        );

        try {

            accountServiceClient.executeTransfer(
                    userId,
                    request
            );

            transaction.setStatus(
                    TransactionStatus.COMPLETED
            );

            transaction.setCompletedAt(
                    LocalDateTime.now()
            );

        } catch (AccountServiceBusinessException ex) {

            transaction.setStatus(
                    TransactionStatus.FAILED
            );

            transactionRepository.save(transaction);

            auditLogService.log(
                    userId,
                    "TRANSFER_FAILED",
                    transaction.getTransactionReference(),
                    "FAILED",
                    ex.getMessage()
            );

            throw new BusinessRuleException(
                    ex.getMessage()
            );

        } catch (AccountServiceUnavailableException ex) {

            transaction.setStatus(
                    TransactionStatus.FAILED
            );

            transactionRepository.save(transaction);

            auditLogService.log(
                    userId,
                    "TRANSFER_FAILED",
                    transaction.getTransactionReference(),
                    "FAILED",
                    ex.getMessage()
            );

            throw new TransactionProcessingException(
                    "Transfer could not be completed because Account Service is unavailable",
                    ex
            );
        }

        /*
         * Persist the final transaction status.
         */
        transactionRepository.save(transaction);

        /*
         * Audit successful transfer only after the transaction
         * has been marked COMPLETED.
         */
        if (transaction.getStatus() ==
                TransactionStatus.COMPLETED) {

            auditLogService.log(
                    userId,
                    "TRANSFER_COMPLETED",
                    transaction.getTransactionReference(),
                    "COMPLETED",
                    "Transfer completed successfully"
            );
        }

        return toResponse(transaction);
    }

    private void validateTransferRequest(
            TransferRequest request
    ) {

        if (request.getSourceAccountId() == null ||
                request.getDestinationAccountId() == null) {

            throw new BusinessRuleException(
                    "Source and destination accounts are required"
            );
        }

        if (request.getSourceAccountId()
                .equals(request.getDestinationAccountId())) {

            throw new BusinessRuleException(
                    "Source and destination accounts must be different"
            );
        }

        if (request.getAmount() == null ||
                request.getAmount()
                        .compareTo(BigDecimal.ZERO) <= 0) {

            throw new BusinessRuleException(
                    "Transfer amount must be greater than zero"
            );
        }

        if (request.getAmount().scale() > 2) {

            throw new BusinessRuleException(
                    "Transfer amount cannot have more than 2 decimal places"
            );
        }

        if (request.getCurrency() == null ||
                request.getCurrency().isBlank()) {

            throw new BusinessRuleException(
                    "Currency is required"
            );
        }
    }

    @Transactional(readOnly = true)
    public List<TransactionHistoryResponse> getAccountHistory(
            Long userId,
            Long accountId
    ) {

        if (accountId == null) {

            throw new BusinessRuleException(
                    "Account ID is required"
            );
        }

        boolean owner =
                accountServiceClient.isAccountOwnedByUser(
                        userId,
                        accountId
                );

        if (!owner) {

            throw new BusinessRuleException(
                    "You do not have access to this account's transaction history"
            );
        }

        return transactionRepository
                .findBySourceAccountIdOrDestinationAccountIdOrderByCreatedAtDesc(
                        accountId,
                        accountId
                )
                .stream()
                .map(this::toHistoryResponse)
                .toList();
    }

    private TransactionResponse toResponse(
            Transaction transaction
    ) {

        return new TransactionResponse(
                transaction.getId(),
                transaction.getTransactionReference(),
                transaction.getIdempotencyKey(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getSourceAccountId(),
                transaction.getDestinationAccountId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getDescription(),
                transaction.getCreatedAt(),
                transaction.getCompletedAt()
        );
    }

    private TransactionHistoryResponse toHistoryResponse(
            Transaction transaction
    ) {

        return new TransactionHistoryResponse(
                transaction.getTransactionReference(),
                transaction.getType(),
                transaction.getStatus(),
                transaction.getSourceAccountId(),
                transaction.getDestinationAccountId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getDescription(),
                transaction.getCreatedAt(),
                transaction.getCompletedAt()
        );
    }
}