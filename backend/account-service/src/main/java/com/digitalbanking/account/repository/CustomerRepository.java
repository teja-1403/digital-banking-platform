package com.digitalbanking.account.repository;

import com.digitalbanking.account.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUserId(Long userId);

    boolean existsByUserId(Long userId);
}