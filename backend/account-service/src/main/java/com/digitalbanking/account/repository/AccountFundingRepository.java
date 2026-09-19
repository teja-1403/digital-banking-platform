package com.digitalbanking.account.repository;

import com.digitalbanking.account.entity.AccountFunding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountFundingRepository
        extends JpaRepository<AccountFunding, Long> {

    Optional<AccountFunding> findByFundingReference(String fundingReference);

    boolean existsByFundingReference(String fundingReference);

    boolean existsByUserId(Long userId);
}