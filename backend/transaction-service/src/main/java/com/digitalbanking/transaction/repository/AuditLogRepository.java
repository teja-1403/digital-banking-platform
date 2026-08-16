package com.digitalbanking.transaction.repository;

import com.digitalbanking.transaction.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository
        extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByUserIdOrderByCreatedAtDesc(
            Long userId
    );

    List<AuditLog> findByTransactionReferenceOrderByCreatedAtDesc(
            String transactionReference
    );
}