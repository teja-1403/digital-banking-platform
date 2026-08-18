package com.digitalbanking.account.repository;

import com.digitalbanking.account.entity.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BeneficiaryRepository
        extends JpaRepository<Beneficiary, Long> {

    List<Beneficiary> findByCustomerId(Long customerId);

    boolean existsByCustomerIdAndBeneficiaryAccountNumber(
            Long customerId,
            String beneficiaryAccountNumber
    );
}