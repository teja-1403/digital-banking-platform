package com.digitalbanking.account.service;

import com.digitalbanking.account.dto.AccountResponse;
import com.digitalbanking.account.dto.CreateAccountRequest;
import com.digitalbanking.account.entity.Account;
import com.digitalbanking.account.entity.AccountStatus;
import com.digitalbanking.account.entity.AccountType;
import com.digitalbanking.account.entity.Customer;
import com.digitalbanking.account.exception.BusinessRuleException;
import com.digitalbanking.account.exception.ResourceNotFoundException;
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
}