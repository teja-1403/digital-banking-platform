package com.digitalbanking.account.repository;

import com.digitalbanking.account.entity.Account;
import com.digitalbanking.account.entity.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

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
}