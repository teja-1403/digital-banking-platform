package com.digitalbanking.transaction.service;

import com.digitalbanking.transaction.entity.AuditLog;
import com.digitalbanking.transaction.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(
            AuditLogRepository auditLogRepository
    ) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(
            Long userId,
            String action,
            String transactionReference,
            String status,
            String message
    ) {

        AuditLog auditLog = new AuditLog();

        auditLog.setUserId(userId);
        auditLog.setAction(action);
        auditLog.setTransactionReference(
                transactionReference
        );
        auditLog.setStatus(status);
        auditLog.setMessage(message);

        auditLogRepository.save(auditLog);
    }
}