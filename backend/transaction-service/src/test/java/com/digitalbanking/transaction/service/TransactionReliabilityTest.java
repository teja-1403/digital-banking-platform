package com.digitalbanking.transaction.service;

import com.digitalbanking.transaction.client.AccountServiceClient;
import com.digitalbanking.transaction.dto.TransactionResponse;
import com.digitalbanking.transaction.dto.TransferRequest;
import com.digitalbanking.transaction.entity.Transaction;
import com.digitalbanking.transaction.entity.TransactionStatus;
import com.digitalbanking.transaction.entity.TransactionType;
import com.digitalbanking.transaction.exception.BusinessRuleException;
import com.digitalbanking.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionReliabilityTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountServiceClient accountServiceClient;

    @Mock
    private TransactionCreationService transactionCreationService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private TransactionService transactionService;

    private TransferRequest request;

    @BeforeEach
    void setUp() {
        request = new TransferRequest();

        request.setSourceAccountId(1L);
        request.setDestinationAccountId(3L);
        request.setAmount(
                new BigDecimal("100.00")
        );
        request.setCurrency("INR");
        request.setDescription("Concurrent transfer");
    }

    @Test
    void shouldReturnExistingTransactionWhenConcurrentInsertWinsRace() {
        String idempotencyKey = "idem-race-001";

        Transaction existingTransaction =
                createTransaction(
                        10L,
                        "TXN-RACE-001",
                        idempotencyKey,
                        TransactionStatus.COMPLETED
                );

        when(
                transactionRepository.findByIdempotencyKey(
                        idempotencyKey
                )
        )
                .thenReturn(
                        Optional.empty(),
                        Optional.of(existingTransaction)
                );

        when(
                transactionCreationService.createPendingTransaction(
                        idempotencyKey,
                        request
                )
        )
                .thenThrow(
                        new DataIntegrityViolationException(
                                "Duplicate idempotency key"
                        )
                );

        TransactionResponse response =
                transactionService.initiateTransfer(
                        1L,
                        idempotencyKey,
                        request
                );

        assertNotNull(response);

        assertEquals(
                10L,
                response.getId()
        );

        assertEquals(
                "TXN-RACE-001",
                response.getTransactionReference()
        );

        assertEquals(
                TransactionStatus.COMPLETED,
                response.getStatus()
        );

        verify(
                transactionRepository,
                times(2)
        ).findByIdempotencyKey(idempotencyKey);

        verify(
                transactionCreationService
        ).createPendingTransaction(
                idempotencyKey,
                request
        );

        verify(
                accountServiceClient,
                never()
        ).executeTransfer(
                anyLong(),
                any()
        );

        verifyNoInteractions(auditLogService);
    }

    @Test
    void shouldThrowBusinessRuleWhenConcurrentInsertCannotBeResolved() {
        String idempotencyKey = "idem-race-002";

        when(
                transactionRepository.findByIdempotencyKey(
                        idempotencyKey
                )
        )
                .thenReturn(
                        Optional.empty()
                );

        when(
                transactionCreationService.createPendingTransaction(
                        idempotencyKey,
                        request
                )
        )
                .thenThrow(
                        new DataIntegrityViolationException(
                                "Duplicate idempotency key"
                        )
                );

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> transactionService.initiateTransfer(
                                1L,
                                idempotencyKey,
                                request
                        )
                );

        assertEquals(
                "Unable to resolve idempotent transaction",
                exception.getMessage()
        );

        verify(
                transactionRepository,
                times(2)
        ).findByIdempotencyKey(idempotencyKey);

        verify(
                transactionCreationService
        ).createPendingTransaction(
                idempotencyKey,
                request
        );

        verify(
                accountServiceClient,
                never()
        ).executeTransfer(
                anyLong(),
                any()
        );

        verifyNoInteractions(auditLogService);
    }

    private Transaction createTransaction(
            Long id,
            String reference,
            String idempotencyKey,
            TransactionStatus status
    ) {
        Transaction transaction =
                new Transaction();

        transaction.setId(id);

        transaction.setTransactionReference(
                reference
        );

        transaction.setIdempotencyKey(
                idempotencyKey
        );

        transaction.setType(
                TransactionType.TRANSFER
        );

        transaction.setStatus(
                status
        );

        transaction.setSourceAccountId(
                request.getSourceAccountId()
        );

        transaction.setDestinationAccountId(
                request.getDestinationAccountId()
        );

        transaction.setAmount(
                request.getAmount()
        );

        transaction.setCurrency(
                request.getCurrency()
        );

        transaction.setDescription(
                request.getDescription()
        );

        return transaction;
    }
}