package com.digitalbanking.account.service;

import com.digitalbanking.account.dto.AccountResponse;
import com.digitalbanking.account.dto.CreateAccountRequest;
import com.digitalbanking.account.entity.Account;
import com.digitalbanking.account.entity.AccountStatus;
import com.digitalbanking.account.entity.Customer;
import com.digitalbanking.account.exception.BusinessRuleException;
import com.digitalbanking.account.exception.ResourceNotFoundException;
import com.digitalbanking.account.repository.AccountRepository;
import com.digitalbanking.account.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    private final SecureRandom secureRandom = new SecureRandom();

    public AccountService(
            AccountRepository accountRepository,
            CustomerRepository customerRepository
    ) {
        this.accountRepository = accountRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional
    public AccountResponse createAccount(
            Long userId,
            CreateAccountRequest request
    ) {

        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer profile not found"
                        )
                );

        String accountNumber = generateUniqueAccountNumber();

        Account account = new Account();

        account.setCustomer(customer);
        account.setAccountNumber(accountNumber);
        account.setAccountType(request.getAccountType());
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency("INR");
        account.setStatus(AccountStatus.ACTIVE);

        Account savedAccount = accountRepository.save(account);

        return toResponse(savedAccount);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> getCurrentUserAccounts(Long userId) {

        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer profile not found"
                        )
                );

        return accountRepository.findByCustomerId(customer.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse getAccount(
            Long userId,
            Long accountId
    ) {

        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer profile not found"
                        )
                );

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account not found"
                        )
                );

        if (!account.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessRuleException(
                    "You do not have access to this account"
            );
        }

        return toResponse(account);
    }

    @Transactional
    public void executeInternalTransfer(
            Long userId,
            Long sourceAccountId,
            Long destinationAccountId,
            BigDecimal amount
    ) {

        if (sourceAccountId.equals(destinationAccountId)) {
            throw new BusinessRuleException(
                    "Source and destination accounts must be different"
            );
        }

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new BusinessRuleException(
                    "Transfer amount must be greater than zero"
            );
        }

        if (amount.scale() > 2) {
            throw new BusinessRuleException(
                    "Transfer amount cannot have more than 2 decimal places"
            );
        }

        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer profile not found"
                        )
                );

        /*
         * Always lock accounts in ascending ID order.
         *
         * This is important for avoiding deadlocks when two
         * transfers happen in opposite directions:
         *
         * Transfer A: account 1 -> account 3
         * Transfer B: account 3 -> account 1
         *
         * Both requests lock 1 first and 3 second.
         */
        Long firstAccountId =
                Math.min(sourceAccountId, destinationAccountId);

        Long secondAccountId =
                Math.max(sourceAccountId, destinationAccountId);

        Account firstAccount =
                accountRepository.findByIdForUpdate(firstAccountId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Account not found"
                                )
                        );

        Account secondAccount =
                accountRepository.findByIdForUpdate(secondAccountId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Account not found"
                                )
                        );

        Account sourceAccount =
                sourceAccountId.equals(firstAccountId)
                        ? firstAccount
                        : secondAccount;

        Account destinationAccount =
                destinationAccountId.equals(firstAccountId)
                        ? firstAccount
                        : secondAccount;

        // Ownership check
        if (!sourceAccount.getCustomer()
                .getId()
                .equals(customer.getId())) {

            throw new BusinessRuleException(
                    "You do not have access to the source account"
            );
        }

        // Source account must be active
        if (sourceAccount.getStatus() != AccountStatus.ACTIVE) {

            throw new BusinessRuleException(
                    "Source account is not active"
            );
        }

        // Destination account must be active
        if (destinationAccount.getStatus() != AccountStatus.ACTIVE) {

            throw new BusinessRuleException(
                    "Destination account is not active"
            );
        }

        // Balance check happens while the row is locked
        if (sourceAccount.getBalance()
                .compareTo(amount) < 0) {

            throw new BusinessRuleException(
                    "Insufficient balance"
            );
        }

        // Debit
        sourceAccount.setBalance(
                sourceAccount.getBalance().subtract(amount)
        );

        // Credit
        destinationAccount.setBalance(
                destinationAccount.getBalance().add(amount)
        );

        accountRepository.save(sourceAccount);
        accountRepository.save(destinationAccount);
    }

    @Transactional(readOnly = true)
    public boolean isAccountOwnedByUser(
            Long userId,
            Long accountId
    ) {

        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer profile not found"
                        )
                );

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Account not found"
                        )
                );

        return account.getCustomer()
                .getId()
                .equals(customer.getId());
    }

    private String generateUniqueAccountNumber() {

        String accountNumber;

        do {
            accountNumber = generateAccountNumber();
        } while (accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    private String generateAccountNumber() {

        StringBuilder accountNumber = new StringBuilder();

        for (int i = 0; i < 12; i++) {
            accountNumber.append(secureRandom.nextInt(10));
        }

        return accountNumber.toString();
    }

    private AccountResponse toResponse(Account account) {

        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getAccountType(),
                account.getBalance(),
                account.getCurrency(),
                account.getStatus()
        );
    }
}