package com.digitalbanking.account.controller;

import com.digitalbanking.account.dto.AdminAccountStatsResponse;
import com.digitalbanking.account.entity.AccountStatus;
import com.digitalbanking.account.repository.AccountRepository;
import com.digitalbanking.account.repository.CustomerRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public AdminController(
            CustomerRepository customerRepository,
            AccountRepository accountRepository
    ) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/account-stats")
    public AdminAccountStatsResponse getAccountStats() {

        long totalCustomers =
                customerRepository.count();

        long totalAccounts =
                accountRepository.count();

        long activeAccounts =
                accountRepository.countByStatus(
                        AccountStatus.ACTIVE
                );

        return new AdminAccountStatsResponse(
                totalCustomers,
                totalAccounts,
                activeAccounts,
                accountRepository.getTotalActiveBalance()
        );
    }
}