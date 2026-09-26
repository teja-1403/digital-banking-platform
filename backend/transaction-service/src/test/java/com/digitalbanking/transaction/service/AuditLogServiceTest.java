package com.digitalbanking.transaction.service;

import com.digitalbanking.transaction.entity.AuditLog;
import com.digitalbanking.transaction.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditLogServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    private AuditLogService auditLogService;

    @BeforeEach
    void setUp() {
        auditLogService =
                new AuditLogService(auditLogRepository);
    }

    @Test
    void log_shouldCreateAndPersistAuditLog() {
        when(auditLogRepository.save(any(AuditLog.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        auditLogService.log(
                1L,
                "TRANSFER_COMPLETED",
                "TXN-20260926-ABC12345",
                "COMPLETED",
                "Transfer completed successfully"
        );

        ArgumentCaptor<AuditLog> captor =
                ArgumentCaptor.forClass(AuditLog.class);

        verify(auditLogRepository)
                .save(captor.capture());

        AuditLog savedAuditLog =
                captor.getValue();

        assertEquals(
                1L,
                savedAuditLog.getUserId()
        );

        assertEquals(
                "TRANSFER_COMPLETED",
                savedAuditLog.getAction()
        );

        assertEquals(
                "TXN-20260926-ABC12345",
                savedAuditLog.getTransactionReference()
        );

        assertEquals(
                "COMPLETED",
                savedAuditLog.getStatus()
        );

        assertEquals(
                "Transfer completed successfully",
                savedAuditLog.getMessage()
        );
    }

    @Test
    void log_shouldPersistFailedTransferAudit() {
        auditLogService.log(
                5L,
                "TRANSFER_FAILED",
                "TXN-20260926-FAIL1234",
                "FAILED",
                "Insufficient balance"
        );

        ArgumentCaptor<AuditLog> captor =
                ArgumentCaptor.forClass(AuditLog.class);

        verify(auditLogRepository)
                .save(captor.capture());

        AuditLog savedAuditLog =
                captor.getValue();

        assertEquals(5L, savedAuditLog.getUserId());
        assertEquals("TRANSFER_FAILED", savedAuditLog.getAction());
        assertEquals(
                "TXN-20260926-FAIL1234",
                savedAuditLog.getTransactionReference()
        );
        assertEquals("FAILED", savedAuditLog.getStatus());
        assertEquals(
                "Insufficient balance",
                savedAuditLog.getMessage()
        );
    }
}