package com.digitalbanking.transaction.service;

import com.digitalbanking.transaction.dto.TransferRequest;
import com.digitalbanking.transaction.entity.Transaction;
import com.digitalbanking.transaction.entity.TransactionStatus;
import com.digitalbanking.transaction.entity.TransactionType;
import com.digitalbanking.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionCreationServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionReferenceGenerator referenceGenerator;

    private TransactionCreationService transactionCreationService;

    private TransferRequest request;

    @BeforeEach
    void setUp() {
        transactionCreationService =
                new TransactionCreationService(
                        transactionRepository,
                        referenceGenerator
                );

        request = new TransferRequest();

        request.setSourceAccountId(1L);
        request.setDestinationAccountId(3L);
        request.setAmount(
                new BigDecimal("250.00")
        );
        request.setCurrency("inr");
        request.setDescription("Test transfer");
    }

    @Test
    void createPendingTransaction_shouldCreateAndPersistPendingTransaction() {
        when(referenceGenerator.generate())
                .thenReturn("TXN-20260926-ABC12345");

        when(transactionRepository.saveAndFlush(
                any(Transaction.class)
        )).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        Transaction result =
                transactionCreationService.createPendingTransaction(
                        "idem-001",
                        request
                );

        assertNotNull(result);

        assertEquals(
                "TXN-20260926-ABC12345",
                result.getTransactionReference()
        );

        assertEquals(
                "idem-001",
                result.getIdempotencyKey()
        );

        assertEquals(
                TransactionType.TRANSFER,
                result.getType()
        );

        assertEquals(
                TransactionStatus.PENDING,
                result.getStatus()
        );

        assertEquals(
                1L,
                result.getSourceAccountId()
        );

        assertEquals(
                3L,
                result.getDestinationAccountId()
        );

        assertEquals(
                new BigDecimal("250.00"),
                result.getAmount()
        );

        assertEquals(
                "INR",
                result.getCurrency()
        );

        assertEquals(
                "Test transfer",
                result.getDescription()
        );

        verify(referenceGenerator)
                .generate();

        verify(transactionRepository)
                .saveAndFlush(any(Transaction.class));
    }

    @Test
    void createPendingTransaction_shouldPersistTheExpectedTransaction() {
        when(referenceGenerator.generate())
                .thenReturn("TXN-TEST-001");

        when(transactionRepository.saveAndFlush(
                any(Transaction.class)
        )).thenAnswer(invocation ->
                invocation.getArgument(0)
        );

        transactionCreationService.createPendingTransaction(
                "idem-002",
                request
        );

        ArgumentCaptor<Transaction> transactionCaptor =
                ArgumentCaptor.forClass(Transaction.class);

        verify(transactionRepository)
                .saveAndFlush(transactionCaptor.capture());

        Transaction savedTransaction =
                transactionCaptor.getValue();

        assertEquals(
                "TXN-TEST-001",
                savedTransaction.getTransactionReference()
        );

        assertEquals(
                "idem-002",
                savedTransaction.getIdempotencyKey()
        );

        assertEquals(
                TransactionStatus.PENDING,
                savedTransaction.getStatus()
        );

        assertEquals(
                TransactionType.TRANSFER,
                savedTransaction.getType()
        );

        assertEquals(
                "INR",
                savedTransaction.getCurrency()
        );

        verify(referenceGenerator)
                .generate();
    }
}