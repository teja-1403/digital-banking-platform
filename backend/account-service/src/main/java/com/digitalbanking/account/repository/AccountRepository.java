package com.digitalbanking.account.repository;

import com.digitalbanking.account.entity.Account;
import com.digitalbanking.account.entity.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByCustomerId(Long customerId);

    List<Account> findByCustomerIdAndStatus(
            Long customerId,
            AccountStatus status
    );

    boolean existsByAccountNumber(String accountNumber);

    @Query("""
        SELECT COALESCE(SUM(a.balance), 0)
        FROM Account a
        WHERE a.status = com.digitalbanking.account.entity.AccountStatus.ACTIVE
        """)
    BigDecimal getTotalActiveBalance();

    long countByStatus(AccountStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdForUpdate(@Param("id") Long id);
}