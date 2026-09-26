package com.digitalbanking.account.service;

import com.digitalbanking.account.dto.AccountResponse;
import com.digitalbanking.account.dto.CreateAccountRequest;
import com.digitalbanking.account.entity.Account;
import com.digitalbanking.account.entity.AccountStatus;
import com.digitalbanking.account.entity.AccountType;
import com.digitalbanking.account.entity.Customer;
import com.digitalbanking.account.exception.BusinessRuleException;
import com.digitalbanking.account.exception.ResourceNotFoundException;
import com.digitalbanking.account.repository.AccountFundingRepository;
import com.digitalbanking.account.dto.FundAccountResponse;
import com.digitalbanking.account.entity.AccountFunding;
import com.digitalbanking.account.repository.AccountRepository;
import com.digitalbanking.account.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountFundingRepository accountFundingRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountService accountService;

    @Test
    void shouldCreateAccount() {

        Long userId = 1L;

        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountType(AccountType.SAVINGS);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);
        customer.setFirstName("Sai");
        customer.setLastName("Teja");

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.existsByAccountNumber(anyString()))
                .thenReturn(false);

        Account savedAccount = new Account();
        savedAccount.setId(1L);
        savedAccount.setCustomer(customer);
        savedAccount.setAccountNumber("123456789012");
        savedAccount.setAccountType(AccountType.SAVINGS);
        savedAccount.setBalance(BigDecimal.ZERO);
        savedAccount.setCurrency("INR");
        savedAccount.setStatus(AccountStatus.ACTIVE);

        when(accountRepository.save(any(Account.class)))
                .thenReturn(savedAccount);

        AccountResponse response =
                accountService.createAccount(userId, request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("123456789012", response.getAccountNumber());
        assertEquals(AccountType.SAVINGS, response.getAccountType());
        assertEquals(BigDecimal.ZERO, response.getBalance());
        assertEquals("INR", response.getCurrency());
        assertEquals(AccountStatus.ACTIVE, response.getStatus());

        verify(customerRepository).findByUserId(userId);
        verify(accountRepository).existsByAccountNumber(anyString());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void shouldRejectAccountCreationWhenCustomerDoesNotExist() {

        Long userId = 999L;

        CreateAccountRequest request = new CreateAccountRequest();
        request.setAccountType(AccountType.SAVINGS);

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> accountService.createAccount(userId, request)
        );

        verify(customerRepository).findByUserId(userId);
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void shouldGetCurrentUserAccounts() {

        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account account1 = createAccount(
                1L,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                BigDecimal.ZERO,
                AccountStatus.ACTIVE
        );

        Account account2 = createAccount(
                2L,
                customer,
                "123456789013",
                AccountType.CURRENT,
                new BigDecimal("5000.00"),
                AccountStatus.ACTIVE
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByCustomerId(customer.getId()))
                .thenReturn(List.of(account1, account2));

        List<AccountResponse> responses =
                accountService.getCurrentUserAccounts(userId);

        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(
                "123456789012",
                responses.get(0).getAccountNumber()
        );

        assertEquals(
                "123456789013",
                responses.get(1).getAccountNumber()
        );

        verify(customerRepository).findByUserId(userId);
        verify(accountRepository).findByCustomerId(customer.getId());
    }

    @Test
    void shouldThrowExceptionWhenGettingAccountsAndCustomerDoesNotExist() {

        Long userId = 999L;

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> accountService.getCurrentUserAccounts(userId)
        );

        verify(customerRepository).findByUserId(userId);
        verify(accountRepository, never()).findByCustomerId(anyLong());
    }

    @Test
    void shouldGetAccountBelongingToCurrentUser() {

        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account account = createAccount(
                1L,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                BigDecimal.ZERO,
                AccountStatus.ACTIVE
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(account));

        AccountResponse response =
                accountService.getAccount(userId, 1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("123456789012", response.getAccountNumber());

        verify(customerRepository).findByUserId(userId);
        verify(accountRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenAccountDoesNotExist() {

        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> accountService.getAccount(userId, 999L)
        );

        verify(customerRepository).findByUserId(userId);
        verify(accountRepository).findById(999L);
    }

    @Test
    void shouldRejectAccessToAnotherUsersAccount() {

        Long currentUserId = 1L;

        Customer currentCustomer = new Customer();
        currentCustomer.setId(1L);
        currentCustomer.setUserId(currentUserId);

        Customer anotherCustomer = new Customer();
        anotherCustomer.setId(2L);
        anotherCustomer.setUserId(3L);

        Account anotherUsersAccount = createAccount(
                2L,
                anotherCustomer,
                "987654321098",
                AccountType.SAVINGS,
                BigDecimal.ZERO,
                AccountStatus.ACTIVE
        );

        when(customerRepository.findByUserId(currentUserId))
                .thenReturn(Optional.of(currentCustomer));

        when(accountRepository.findById(2L))
                .thenReturn(Optional.of(anotherUsersAccount));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> accountService.getAccount(
                        currentUserId,
                        2L
                )
        );

        assertEquals(
                "You do not have access to this account",
                exception.getMessage()
        );

        verify(customerRepository).findByUserId(currentUserId);
        verify(accountRepository).findById(2L);
    }

    private Account createAccount(
            Long id,
            Customer customer,
            String accountNumber,
            AccountType accountType,
            BigDecimal balance,
            AccountStatus status
    ) {

        Account account = new Account();

        account.setId(id);
        account.setCustomer(customer);
        account.setAccountNumber(accountNumber);
        account.setAccountType(accountType);
        account.setBalance(balance);
        account.setCurrency("INR");
        account.setStatus(status);

        return account;
    }

    @Test
    void shouldFundActiveAccount() {

        Long userId = 1L;
        Long accountId = 10L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account account = createAccount(
                accountId,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                new BigDecimal("5000.00"),
                AccountStatus.ACTIVE
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByIdForUpdate(accountId))
                .thenReturn(Optional.of(account));

        when(accountFundingRepository.existsByFundingReference(anyString()))
                .thenReturn(false);

        AccountFunding savedFunding = new AccountFunding();
        savedFunding.setId(1L);
        savedFunding.setFundingReference("FND-TEST");
        savedFunding.setAccountId(accountId);
        savedFunding.setUserId(userId);
        savedFunding.setAmount(new BigDecimal("10000.00"));
        savedFunding.setBalanceAfter(new BigDecimal("15000.00"));
        savedFunding.setCurrency("INR");

        when(accountFundingRepository.save(any(AccountFunding.class)))
                .thenReturn(savedFunding);

        FundAccountResponse response =
                accountService.fundAccount(
                        userId,
                        accountId,
                        new BigDecimal("10000.00")
                );

        assertNotNull(response);
        assertEquals(accountId, response.getAccountId());
        assertEquals("123456789012", response.getAccountNumber());
        assertEquals(new BigDecimal("10000.00"), response.getAmount());
        assertEquals(new BigDecimal("15000.00"), response.getBalance());
        assertEquals("INR", response.getCurrency());

        assertEquals(
                new BigDecimal("15000.00"),
                account.getBalance()
        );

        verify(customerRepository).findByUserId(userId);
        verify(accountRepository).findByIdForUpdate(accountId);
        verify(accountRepository).save(account);
        verify(accountFundingRepository).save(any(AccountFunding.class));
    }

    @Test
    void shouldRejectFundingWhenAmountIsNotPositive() {

        Long userId = 1L;
        Long accountId = 10L;

        assertThrows(
                BusinessRuleException.class,
                () -> accountService.fundAccount(
                        userId,
                        accountId,
                        BigDecimal.ZERO
                )
        );

        verifyNoInteractions(
                customerRepository,
                accountRepository,
                accountFundingRepository
        );
    }

    @Test
    void shouldRejectFundingWhenAmountHasMoreThanTwoDecimals() {

        Long userId = 1L;
        Long accountId = 10L;

        assertThrows(
                BusinessRuleException.class,
                () -> accountService.fundAccount(
                        userId,
                        accountId,
                        new BigDecimal("100.123")
                )
        );

        verifyNoInteractions(
                customerRepository,
                accountRepository,
                accountFundingRepository
        );
    }

    @Test
    void shouldRejectFundingWhenAccountIsNotActive() {

        Long userId = 1L;
        Long accountId = 10L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account account = createAccount(
                accountId,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                new BigDecimal("5000.00"),
                AccountStatus.BLOCKED
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByIdForUpdate(accountId))
                .thenReturn(Optional.of(account));

        assertThrows(
                BusinessRuleException.class,
                () -> accountService.fundAccount(
                        userId,
                        accountId,
                        new BigDecimal("1000.00")
                )
        );

        assertEquals(
                new BigDecimal("5000.00"),
                account.getBalance()
        );

        verify(accountRepository, never()).save(any(Account.class));
        verify(accountFundingRepository, never()).save(any(AccountFunding.class));
    }

    @Test
    void shouldRejectTransferWhenSourceAccountHasInsufficientBalance() {

        Long userId = 1L;
        Long sourceAccountId = 1L;
        Long destinationAccountId = 2L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account sourceAccount = createAccount(
                sourceAccountId,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                new BigDecimal("5000.00"),
                AccountStatus.ACTIVE
        );

        Customer destinationCustomer = new Customer();
        destinationCustomer.setId(2L);
        destinationCustomer.setUserId(2L);

        Account destinationAccount = createAccount(
                destinationAccountId,
                destinationCustomer,
                "987654321098",
                AccountType.SAVINGS,
                new BigDecimal("2000.00"),
                AccountStatus.ACTIVE
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByIdForUpdate(sourceAccountId))
                .thenReturn(Optional.of(sourceAccount));

        when(accountRepository.findByIdForUpdate(destinationAccountId))
                .thenReturn(Optional.of(destinationAccount));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> accountService.executeInternalTransfer(
                        userId,
                        sourceAccountId,
                        destinationAccountId,
                        new BigDecimal("7000.00")
                )
        );

        assertEquals(
                "Insufficient balance",
                exception.getMessage()
        );

        // Neither account should be mutated.
        assertEquals(
                new BigDecimal("5000.00"),
                sourceAccount.getBalance()
        );

        assertEquals(
                new BigDecimal("2000.00"),
                destinationAccount.getBalance()
        );

        verify(customerRepository).findByUserId(userId);

        verify(accountRepository)
                .findByIdForUpdate(sourceAccountId);

        verify(accountRepository)
                .findByIdForUpdate(destinationAccountId);

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    @Test
    void shouldFreezeActiveAccount() {

        Long userId = 1L;
        Long accountId = 10L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account account = createAccount(
                accountId,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                new BigDecimal("5000.00"),
                AccountStatus.ACTIVE
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByIdForUpdate(accountId))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(account))
                .thenReturn(account);

        AccountResponse response =
                accountService.freezeAccount(userId, accountId);

        assertEquals(AccountStatus.BLOCKED, account.getStatus());
        assertEquals(AccountStatus.BLOCKED, response.getStatus());

        verify(customerRepository).findByUserId(userId);
        verify(accountRepository).findByIdForUpdate(accountId);
        verify(accountRepository).save(account);
    }

    @Test
    void shouldRejectFreezingNonActiveAccount() {

        Long userId = 1L;
        Long accountId = 10L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account account = createAccount(
                accountId,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                new BigDecimal("5000.00"),
                AccountStatus.BLOCKED
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByIdForUpdate(accountId))
                .thenReturn(Optional.of(account));

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> accountService.freezeAccount(
                                userId,
                                accountId
                        )
                );

        assertEquals(
                "Only active accounts can be frozen",
                exception.getMessage()
        );

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    @Test
    void shouldActivateBlockedAccount() {

        Long userId = 1L;
        Long accountId = 10L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account account = createAccount(
                accountId,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                new BigDecimal("5000.00"),
                AccountStatus.BLOCKED
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByIdForUpdate(accountId))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(account))
                .thenReturn(account);

        AccountResponse response =
                accountService.activateAccount(userId, accountId);

        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertEquals(AccountStatus.ACTIVE, response.getStatus());

        verify(accountRepository).save(account);
    }

    @Test
    void shouldRejectActivatingActiveAccount() {

        Long userId = 1L;
        Long accountId = 10L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account account = createAccount(
                accountId,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                new BigDecimal("5000.00"),
                AccountStatus.ACTIVE
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByIdForUpdate(accountId))
                .thenReturn(Optional.of(account));

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> accountService.activateAccount(
                                userId,
                                accountId
                        )
                );

        assertEquals(
                "Only blocked accounts can be activated",
                exception.getMessage()
        );

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    @Test
    void shouldCloseZeroBalanceActiveAccount() {

        Long userId = 1L;
        Long accountId = 10L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account account = createAccount(
                accountId,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                BigDecimal.ZERO,
                AccountStatus.ACTIVE
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByIdForUpdate(accountId))
                .thenReturn(Optional.of(account));

        when(accountRepository.save(account))
                .thenReturn(account);

        AccountResponse response =
                accountService.closeAccount(userId, accountId);

        assertEquals(AccountStatus.CLOSED, account.getStatus());
        assertEquals(AccountStatus.CLOSED, response.getStatus());

        verify(accountRepository).save(account);
    }

    @Test
    void shouldRejectClosingActiveAccountWithNonZeroBalance() {

        Long userId = 1L;
        Long accountId = 10L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account account = createAccount(
                accountId,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                new BigDecimal("1000.00"),
                AccountStatus.ACTIVE
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByIdForUpdate(accountId))
                .thenReturn(Optional.of(account));

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> accountService.closeAccount(
                                userId,
                                accountId
                        )
                );

        assertEquals(
                "Account must have zero balance before it can be closed",
                exception.getMessage()
        );

        assertEquals(
                new BigDecimal("1000.00"),
                account.getBalance()
        );

        assertEquals(
                AccountStatus.ACTIVE,
                account.getStatus()
        );

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    @Test
    void shouldRejectClosingBlockedAccount() {

        Long userId = 1L;
        Long accountId = 10L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account account = createAccount(
                accountId,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                BigDecimal.ZERO,
                AccountStatus.BLOCKED
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByIdForUpdate(accountId))
                .thenReturn(Optional.of(account));

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> accountService.closeAccount(
                                userId,
                                accountId
                        )
                );

        assertEquals(
                "Blocked accounts must be activated before they can be closed",
                exception.getMessage()
        );

        assertEquals(
                AccountStatus.BLOCKED,
                account.getStatus()
        );

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    @Test
    void shouldRejectActivatingClosedAccount() {

        Long userId = 1L;
        Long accountId = 10L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account account = createAccount(
                accountId,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                BigDecimal.ZERO,
                AccountStatus.CLOSED
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByIdForUpdate(accountId))
                .thenReturn(Optional.of(account));

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> accountService.activateAccount(
                                userId,
                                accountId
                        )
                );

        assertEquals(
                "Only blocked accounts can be activated",
                exception.getMessage()
        );

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    @Test
    void shouldRejectFreezingClosedAccount() {

        Long userId = 1L;
        Long accountId = 10L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account account = createAccount(
                accountId,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                BigDecimal.ZERO,
                AccountStatus.CLOSED
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByIdForUpdate(accountId))
                .thenReturn(Optional.of(account));

        BusinessRuleException exception =
                assertThrows(
                        BusinessRuleException.class,
                        () -> accountService.freezeAccount(
                                userId,
                                accountId
                        )
                );

        assertEquals(
                "Only active accounts can be frozen",
                exception.getMessage()
        );

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    @Test
    void shouldRejectFundingWhenAccountIsClosed() {

        Long userId = 1L;
        Long accountId = 10L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account account = createAccount(
                accountId,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                BigDecimal.ZERO,
                AccountStatus.CLOSED
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByIdForUpdate(accountId))
                .thenReturn(Optional.of(account));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> accountService.fundAccount(
                        userId,
                        accountId,
                        new BigDecimal("1000.00")
                )
        );

        assertEquals(
                "Only active accounts can be funded",
                exception.getMessage()
        );

        assertEquals(
                BigDecimal.ZERO,
                account.getBalance()
        );

        verify(accountRepository, never())
                .save(any(Account.class));

        verify(accountFundingRepository, never())
                .save(any(AccountFunding.class));
    }

    @Test
    void shouldRejectTransferWhenSourceAccountIsBlocked() {

        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account sourceAccount = createAccount(
                1L,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                new BigDecimal("5000.00"),
                AccountStatus.BLOCKED
        );

        Customer destinationCustomer = new Customer();
        destinationCustomer.setId(2L);
        destinationCustomer.setUserId(2L);

        Account destinationAccount = createAccount(
                2L,
                destinationCustomer,
                "987654321098",
                AccountType.SAVINGS,
                new BigDecimal("2000.00"),
                AccountStatus.ACTIVE
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(accountRepository.findByIdForUpdate(2L))
                .thenReturn(Optional.of(destinationAccount));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> accountService.executeInternalTransfer(
                        userId,
                        1L,
                        2L,
                        new BigDecimal("1000.00")
                )
        );

        assertEquals(
                "Source account is not active",
                exception.getMessage()
        );

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    @Test
    void shouldRejectTransferWhenSourceAccountIsClosed() {

        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account sourceAccount = createAccount(
                1L,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                BigDecimal.ZERO,
                AccountStatus.CLOSED
        );

        Customer destinationCustomer = new Customer();
        destinationCustomer.setId(2L);
        destinationCustomer.setUserId(2L);

        Account destinationAccount = createAccount(
                2L,
                destinationCustomer,
                "987654321098",
                AccountType.SAVINGS,
                new BigDecimal("2000.00"),
                AccountStatus.ACTIVE
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(accountRepository.findByIdForUpdate(2L))
                .thenReturn(Optional.of(destinationAccount));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> accountService.executeInternalTransfer(
                        userId,
                        1L,
                        2L,
                        new BigDecimal("1000.00")
                )
        );

        assertEquals(
                "Source account is not active",
                exception.getMessage()
        );

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    @Test
    void shouldRejectTransferWhenDestinationAccountIsBlocked() {

        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account sourceAccount = createAccount(
                1L,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                new BigDecimal("5000.00"),
                AccountStatus.ACTIVE
        );

        Customer destinationCustomer = new Customer();
        destinationCustomer.setId(2L);
        destinationCustomer.setUserId(2L);

        Account destinationAccount = createAccount(
                2L,
                destinationCustomer,
                "987654321098",
                AccountType.SAVINGS,
                new BigDecimal("2000.00"),
                AccountStatus.BLOCKED
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(accountRepository.findByIdForUpdate(2L))
                .thenReturn(Optional.of(destinationAccount));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> accountService.executeInternalTransfer(
                        userId,
                        1L,
                        2L,
                        new BigDecimal("1000.00")
                )
        );

        assertEquals(
                "Destination account is not active",
                exception.getMessage()
        );

        verify(accountRepository, never())
                .save(any(Account.class));
    }

    @Test
    void shouldRejectTransferWhenDestinationAccountIsClosed() {

        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account sourceAccount = createAccount(
                1L,
                customer,
                "123456789012",
                AccountType.SAVINGS,
                new BigDecimal("5000.00"),
                AccountStatus.ACTIVE
        );

        Customer destinationCustomer = new Customer();
        destinationCustomer.setId(2L);
        destinationCustomer.setUserId(2L);

        Account destinationAccount = createAccount(
                2L,
                destinationCustomer,
                "987654321098",
                AccountType.SAVINGS,
                BigDecimal.ZERO,
                AccountStatus.CLOSED
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(accountRepository.findByIdForUpdate(2L))
                .thenReturn(Optional.of(destinationAccount));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> accountService.executeInternalTransfer(
                        userId,
                        1L,
                        2L,
                        new BigDecimal("1000.00")
                )
        );

        assertEquals(
                "Destination account is not active",
                exception.getMessage()
        );

        verify(accountRepository, never())
                .save(any(Account.class));
    }
}