package com.digitalbanking.transaction.service;

import com.digitalbanking.transaction.client.AccountServiceClient;
import com.digitalbanking.transaction.dto.TransactionResponse;
import com.digitalbanking.transaction.dto.TransferRequest;
import com.digitalbanking.transaction.entity.Transaction;
import com.digitalbanking.transaction.entity.TransactionStatus;
import com.digitalbanking.transaction.entity.TransactionType;
import com.digitalbanking.transaction.exception.BusinessRuleException;
import com.digitalbanking.transaction.exception.AccountServiceBusinessException;
import com.digitalbanking.transaction.exception.AccountServiceUnavailableException;
import com.digitalbanking.transaction.exception.TransactionProcessingException;
import com.digitalbanking.transaction.repository.TransactionRepository;
import com.digitalbanking.transaction.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

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
        request.setDescription("Test transfer");
    }

    @Test
    void shouldCompleteTransferSuccessfully() {

        Transaction transaction =
                createTransaction(
                        1L,
                        "TXN-TEST-001",
                        "idem-001",
                        TransactionStatus.PENDING
                );

        when(
                transactionCreationService
                        .createPendingTransaction(
                                "idem-001",
                                request
                        )
        ).thenReturn(transaction);

        doNothing()
                .when(accountServiceClient)
                .executeTransfer(
                        1L,
                        request
                );

        TransactionResponse response =
                transactionService.initiateTransfer(
                        1L,
                        "idem-001",
                        request
                );

        assertNotNull(response);
        assertEquals(
                TransactionStatus.COMPLETED,
                transaction.getStatus()
        );

        assertNotNull(
                transaction.getCompletedAt()
        );

        verify(accountServiceClient)
                .executeTransfer(
                        1L,
                        request
                );

        verify(transactionRepository)
                .save(transaction);

        verify(auditLogService)
                .log(
                        1L,
                        "TRANSFER_INITIATED",
                        "TXN-TEST-001",
                        "PENDING",
                        "Transfer initiated"
                );

        verify(auditLogService)
                .log(
                        1L,
                        "TRANSFER_COMPLETED",
                        "TXN-TEST-001",
                        "COMPLETED",
                        "Transfer completed successfully"
                );
    }

    @Test
    void shouldFailTransferWhenAccountServiceRejectsRequest() {

        Transaction transaction =
                createTransaction(
                        2L,
                        "TXN-TEST-002",
                        "idem-002",
                        TransactionStatus.PENDING
                );

        when(
                transactionCreationService
                        .createPendingTransaction(
                                "idem-002",
                                request
                        )
        ).thenReturn(transaction);

        doThrow(
                new AccountServiceBusinessException(
                        "Insufficient balance"
                )
        ).when(accountServiceClient)
                .executeTransfer(
                        1L,
                        request
                );

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> transactionService.initiateTransfer(
                                1L,
                                "idem-002",
                                request
                        )
                );

        assertEquals(
                "Insufficient balance",
                exception.getMessage()
        );

        assertEquals(
                TransactionStatus.FAILED,
                transaction.getStatus()
        );

        verify(transactionRepository)
                .save(transaction);

        verify(auditLogService)
                .log(
                        1L,
                        "TRANSFER_FAILED",
                        "TXN-TEST-002",
                        "FAILED",
                        "Insufficient balance"
                );

        verify(
                auditLogService,
                never()
        ).log(
                1L,
                "TRANSFER_COMPLETED",
                "TXN-TEST-002",
                "COMPLETED",
                "Transfer completed successfully"
        );
    }

    @Test
    void shouldFailWithProcessingErrorWhenAccountServiceUnavailable() {

        Transaction transaction =
                createTransaction(
                        3L,
                        "TXN-TEST-003",
                        "idem-003",
                        TransactionStatus.PENDING
                );

        when(
                transactionCreationService
                        .createPendingTransaction(
                                "idem-003",
                                request
                        )
        ).thenReturn(transaction);

        doThrow(
                new AccountServiceUnavailableException(
                        "Account Service unavailable"
                )
        ).when(accountServiceClient)
                .executeTransfer(
                        1L,
                        request
                );

        TransactionProcessingException exception =
                assertThrows(
                        TransactionProcessingException.class,
                        () -> transactionService.initiateTransfer(
                                1L,
                                "idem-003",
                                request
                        )
                );

        assertEquals(
                "Transfer could not be completed because Account Service is unavailable",
                exception.getMessage()
        );

        assertEquals(
                TransactionStatus.FAILED,
                transaction.getStatus()
        );

        verify(transactionRepository)
                .save(transaction);
    }

    @Test
    void shouldReturnExistingTransactionForIdempotencyKey() {

        Transaction existingTransaction =
                createTransaction(
                        4L,
                        "TXN-TEST-004",
                        "idem-existing",
                        TransactionStatus.COMPLETED
                );

        existingTransaction.setCompletedAt(
                LocalDateTime.now()
        );

        when(
                transactionRepository
                        .findByIdempotencyKey(
                                "idem-existing"
                        )
        ).thenReturn(
                Optional.of(existingTransaction)
        );

        TransactionResponse response =
                transactionService.initiateTransfer(
                        1L,
                        "idem-existing",
                        request
                );

        assertNotNull(response);

        assertEquals(
                4L,
                response.getId()
        );

        assertEquals(
                TransactionStatus.COMPLETED,
                response.getStatus()
        );

        verify(
                transactionCreationService,
                never()
        ).createPendingTransaction(
                any(),
                any()
        );

        verify(
                accountServiceClient,
                never()
        ).executeTransfer(
                anyLong(),
                any()
        );
    }

    @Test
    void shouldRejectMissingIdempotencyKey() {

        assertThrows(
                BusinessRuleException.class,
                () -> transactionService.initiateTransfer(
                        1L,
                        null,
                        request
                )
        );

        verifyNoInteractions(
                transactionCreationService,
                accountServiceClient
        );
    }

    @Test
    void shouldRejectSameSourceAndDestination() {

        request.setDestinationAccountId(1L);

        assertThrows(
                BusinessRuleException.class,
                () -> transactionService.initiateTransfer(
                        1L,
                        "idem-same-account",
                        request
                )
        );

        verifyNoInteractions(
                transactionCreationService,
                accountServiceClient
        );
    }

    @Test
    void shouldRejectZeroAmount() {

        request.setAmount(
                BigDecimal.ZERO
        );

        assertThrows(
                BusinessRuleException.class,
                () -> transactionService.initiateTransfer(
                        1L,
                        "idem-zero",
                        request
                )
        );

        verifyNoInteractions(
                transactionCreationService,
                accountServiceClient
        );
    }

    @Test
    void shouldRejectMoreThanTwoDecimalPlaces() {

        request.setAmount(
                new BigDecimal("100.123")
        );

        assertThrows(
                BusinessRuleException.class,
                () -> transactionService.initiateTransfer(
                        1L,
                        "idem-scale",
                        request
                )
        );

        verifyNoInteractions(
                transactionCreationService,
                accountServiceClient
        );
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
        transaction.setStatus(status);
        transaction.setSourceAccountId(1L);
        transaction.setDestinationAccountId(3L);
        transaction.setAmount(
                new BigDecimal("100.00")
        );
        transaction.setCurrency("INR");
        transaction.setDescription(
                "Test transfer"
        );

        return transaction;
    }

    @Test
    void shouldReturnTransactionDetailsWhenUserOwnsSourceAccount() {

        Transaction transaction =
                createTransaction(
                        6L,
                        "TXN-DETAIL-001",
                        "idem-detail-001",
                        TransactionStatus.COMPLETED
                );

        transaction.setCreatedAt(
                LocalDateTime.of(
                        2026,
                        9,
                        20,
                        10,
                        0
                )
        );

        transaction.setCompletedAt(
                LocalDateTime.of(
                        2026,
                        9,
                        20,
                        10,
                        1
                )
        );

        when(
                transactionRepository
                        .findByTransactionReference(
                                "TXN-DETAIL-001"
                        )
        ).thenReturn(
                Optional.of(transaction)
        );

        when(
                accountServiceClient
                        .isAccountOwnedByUser(
                                1L,
                                1L
                        )
        ).thenReturn(true);

        TransactionResponse response =
                transactionService.getTransactionDetails(
                        1L,
                        "TXN-DETAIL-001"
                );

        assertNotNull(response);

        assertEquals(
                6L,
                response.getId()
        );

        assertEquals(
                "TXN-DETAIL-001",
                response.getTransactionReference()
        );

        assertEquals(
                TransactionStatus.COMPLETED,
                response.getStatus()
        );

        assertEquals(
                new BigDecimal("100.00"),
                response.getAmount()
        );

        verify(accountServiceClient)
                .isAccountOwnedByUser(
                        1L,
                        1L
                );

        verify(
                accountServiceClient,
                never()
        ).isAccountOwnedByUser(
                1L,
                3L
        );
    }

    @Test
    void shouldReturnTransactionDetailsWhenUserOwnsDestinationAccount() {

        Transaction transaction =
                createTransaction(
                        7L,
                        "TXN-DETAIL-002",
                        "idem-detail-002",
                        TransactionStatus.COMPLETED
                );

        when(
                transactionRepository
                        .findByTransactionReference(
                                "TXN-DETAIL-002"
                        )
        ).thenReturn(
                Optional.of(transaction)
        );

        when(
                accountServiceClient
                        .isAccountOwnedByUser(
                                2L,
                                1L
                        )
        ).thenReturn(false);

        when(
                accountServiceClient
                        .isAccountOwnedByUser(
                                2L,
                                3L
                        )
        ).thenReturn(true);

        TransactionResponse response =
                transactionService.getTransactionDetails(
                        2L,
                        "TXN-DETAIL-002"
                );

        assertNotNull(response);

        assertEquals(
                "TXN-DETAIL-002",
                response.getTransactionReference()
        );

        verify(accountServiceClient)
                .isAccountOwnedByUser(
                        2L,
                        1L
                );

        verify(accountServiceClient)
                .isAccountOwnedByUser(
                        2L,
                        3L
                );
    }

    @Test
    void shouldThrowResourceNotFoundWhenTransactionDoesNotExist() {

        when(
                transactionRepository
                        .findByTransactionReference(
                                "TXN-NOT-FOUND"
                        )
        ).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () ->
                                transactionService
                                        .getTransactionDetails(
                                                1L,
                                                "TXN-NOT-FOUND"
                                        )
                );

        assertEquals(
                "Transaction not found",
                exception.getMessage()
        );

        verifyNoInteractions(
                accountServiceClient
        );
    }

    @Test
    void shouldRejectTransactionDetailsWhenUserOwnsNeitherAccount() {

        Transaction transaction =
                createTransaction(
                        8L,
                        "TXN-DETAIL-003",
                        "idem-detail-003",
                        TransactionStatus.FAILED
                );

        when(
                transactionRepository
                        .findByTransactionReference(
                                "TXN-DETAIL-003"
                        )
        ).thenReturn(
                Optional.of(transaction)
        );

        when(
                accountServiceClient
                        .isAccountOwnedByUser(
                                99L,
                                1L
                        )
        ).thenReturn(false);

        when(
                accountServiceClient
                        .isAccountOwnedByUser(
                                99L,
                                3L
                        )
        ).thenReturn(false);

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () ->
                                transactionService
                                        .getTransactionDetails(
                                                99L,
                                                "TXN-DETAIL-003"
                                        )
                );

        assertEquals(
                "You do not have access to this transaction",
                exception.getMessage()
        );

        verify(accountServiceClient)
                .isAccountOwnedByUser(
                        99L,
                        1L
                );

        verify(accountServiceClient)
                .isAccountOwnedByUser(
                        99L,
                        3L
                );
    }

    @Test
    void shouldReturnFailedTransactionResponseWhenAccountServiceRejectsTransfer() {

        Transaction transaction =
                createTransaction(
                        5L,
                        "TXN-TEST-005",
                        "idem-005",
                        TransactionStatus.PENDING
                );

        when(
                transactionCreationService
                        .createPendingTransaction(
                                "idem-005",
                                request
                        )
        ).thenReturn(transaction);

        doThrow(
                new AccountServiceBusinessException(
                        "Insufficient balance"
                )
        ).when(accountServiceClient)
                .executeTransfer(
                        1L,
                        request
                );

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> transactionService.initiateTransfer(
                                1L,
                                "idem-005",
                                request
                        )
                );

        assertEquals(
                "Insufficient balance",
                exception.getMessage()
        );

        assertEquals(
                TransactionStatus.FAILED,
                transaction.getStatus()
        );

        assertNull(transaction.getCompletedAt());

        verify(transactionRepository)
                .save(transaction);

        verify(auditLogService)
                .log(
                        1L,
                        "TRANSFER_FAILED",
                        "TXN-TEST-005",
                        "FAILED",
                        "Insufficient balance"
                );

        verify(
                auditLogService,
                never()
        ).log(
                1L,
                "TRANSFER_COMPLETED",
                "TXN-TEST-005",
                "COMPLETED",
                "Transfer completed successfully"
        );
    }
}