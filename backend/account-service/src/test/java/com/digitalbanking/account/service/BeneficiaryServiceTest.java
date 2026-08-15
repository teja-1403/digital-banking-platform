package com.digitalbanking.account.service;

import com.digitalbanking.account.dto.BeneficiaryResponse;
import com.digitalbanking.account.dto.CreateBeneficiaryRequest;
import com.digitalbanking.account.entity.Account;
import com.digitalbanking.account.entity.AccountStatus;
import com.digitalbanking.account.entity.AccountType;
import com.digitalbanking.account.entity.Beneficiary;
import com.digitalbanking.account.entity.Customer;
import com.digitalbanking.account.exception.BusinessRuleException;
import com.digitalbanking.account.exception.ResourceNotFoundException;
import com.digitalbanking.account.repository.AccountRepository;
import com.digitalbanking.account.repository.BeneficiaryRepository;
import com.digitalbanking.account.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeneficiaryServiceTest {

    @Mock
    private BeneficiaryRepository beneficiaryRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private BeneficiaryService beneficiaryService;

    @Test
    void shouldCreateBeneficiary() {

        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Customer beneficiaryOwner = new Customer();
        beneficiaryOwner.setId(2L);
        beneficiaryOwner.setUserId(3L);

        Account beneficiaryAccount = createAccount(
                2L,
                beneficiaryOwner,
                "987654321098"
        );

        CreateBeneficiaryRequest request =
                new CreateBeneficiaryRequest();

        request.setBeneficiaryAccountNumber("987654321098");
        request.setNickname("Second User");

        Beneficiary savedBeneficiary =
                new Beneficiary();

        savedBeneficiary.setId(1L);
        savedBeneficiary.setCustomer(customer);
        savedBeneficiary.setBeneficiaryAccountNumber(
                "987654321098"
        );
        savedBeneficiary.setNickname("Second User");

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByAccountNumber(
                "987654321098"
        )).thenReturn(Optional.of(beneficiaryAccount));

        when(beneficiaryRepository
                .existsByCustomerIdAndBeneficiaryAccountNumber(
                        customer.getId(),
                        "987654321098"
                ))
                .thenReturn(false);

        when(beneficiaryRepository.save(any(Beneficiary.class)))
                .thenReturn(savedBeneficiary);

        BeneficiaryResponse response =
                beneficiaryService.createBeneficiary(
                        userId,
                        request
                );

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(
                "987654321098",
                response.getBeneficiaryAccountNumber()
        );
        assertEquals(
                "Second User",
                response.getNickname()
        );

        verify(customerRepository).findByUserId(userId);
        verify(accountRepository)
                .findByAccountNumber("987654321098");

        verify(beneficiaryRepository)
                .existsByCustomerIdAndBeneficiaryAccountNumber(
                        customer.getId(),
                        "987654321098"
                );

        verify(beneficiaryRepository)
                .save(any(Beneficiary.class));
    }

    @Test
    void shouldRejectWhenCustomerDoesNotExist() {

        Long userId = 999L;

        CreateBeneficiaryRequest request =
                new CreateBeneficiaryRequest();

        request.setBeneficiaryAccountNumber("987654321098");
        request.setNickname("Second User");

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> beneficiaryService.createBeneficiary(
                        userId,
                        request
                )
        );

        verify(customerRepository).findByUserId(userId);
        verify(beneficiaryRepository, never())
                .save(any(Beneficiary.class));
    }

    @Test
    void shouldRejectWhenBeneficiaryAccountDoesNotExist() {

        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        CreateBeneficiaryRequest request =
                new CreateBeneficiaryRequest();

        request.setBeneficiaryAccountNumber("999999999999");
        request.setNickname("Unknown");

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByAccountNumber(
                "999999999999"
        )).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> beneficiaryService.createBeneficiary(
                        userId,
                        request
                )
        );

        verify(accountRepository)
                .findByAccountNumber("999999999999");

        verify(beneficiaryRepository, never())
                .save(any(Beneficiary.class));
    }

    @Test
    void shouldRejectInactiveBeneficiaryAccount() {

        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Customer beneficiaryOwner = new Customer();
        beneficiaryOwner.setId(2L);

        Account account = createAccount(
                2L,
                beneficiaryOwner,
                "987654321098"
        );

        account.setStatus(AccountStatus.BLOCKED);

        CreateBeneficiaryRequest request =
                new CreateBeneficiaryRequest();

        request.setBeneficiaryAccountNumber("987654321098");
        request.setNickname("Blocked Account");

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByAccountNumber(
                "987654321098"
        )).thenReturn(Optional.of(account));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> beneficiaryService.createBeneficiary(
                        userId,
                        request
                )
        );

        assertEquals(
                "Beneficiary account is not active",
                exception.getMessage()
        );

        verify(beneficiaryRepository, never())
                .save(any(Beneficiary.class));
    }

    @Test
    void shouldRejectOwnAccountAsBeneficiary() {

        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Account ownAccount = createAccount(
                1L,
                customer,
                "123456789012"
        );

        CreateBeneficiaryRequest request =
                new CreateBeneficiaryRequest();

        request.setBeneficiaryAccountNumber("123456789012");
        request.setNickname("My Own Account");

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByAccountNumber(
                "123456789012"
        )).thenReturn(Optional.of(ownAccount));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> beneficiaryService.createBeneficiary(
                        userId,
                        request
                )
        );

        assertEquals(
                "You cannot add your own account as a beneficiary",
                exception.getMessage()
        );

        verify(beneficiaryRepository, never())
                .save(any(Beneficiary.class));
    }

    @Test
    void shouldRejectDuplicateBeneficiary() {

        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Customer beneficiaryOwner = new Customer();
        beneficiaryOwner.setId(2L);

        Account beneficiaryAccount = createAccount(
                2L,
                beneficiaryOwner,
                "987654321098"
        );

        CreateBeneficiaryRequest request =
                new CreateBeneficiaryRequest();

        request.setBeneficiaryAccountNumber("987654321098");
        request.setNickname("Second User");

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(accountRepository.findByAccountNumber(
                "987654321098"
        )).thenReturn(Optional.of(beneficiaryAccount));

        when(beneficiaryRepository
                .existsByCustomerIdAndBeneficiaryAccountNumber(
                        customer.getId(),
                        "987654321098"
                ))
                .thenReturn(true);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> beneficiaryService.createBeneficiary(
                        userId,
                        request
                )
        );

        assertEquals(
                "Beneficiary already exists",
                exception.getMessage()
        );

        verify(beneficiaryRepository, never())
                .save(any(Beneficiary.class));
    }

    @Test
    void shouldGetCurrentUserBeneficiaries() {

        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Beneficiary beneficiary1 = createBeneficiary(
                1L,
                customer,
                "987654321098",
                "Second User"
        );

        Beneficiary beneficiary2 = createBeneficiary(
                2L,
                customer,
                "555555555555",
                "Savings Account"
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(beneficiaryRepository.findByCustomerId(customer.getId()))
                .thenReturn(List.of(beneficiary1, beneficiary2));

        List<BeneficiaryResponse> responses =
                beneficiaryService.getCurrentUserBeneficiaries(userId);

        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(
                "987654321098",
                responses.get(0).getBeneficiaryAccountNumber()
        );

        assertEquals(
                "Second User",
                responses.get(0).getNickname()
        );

        assertEquals(
                "555555555555",
                responses.get(1).getBeneficiaryAccountNumber()
        );

        verify(customerRepository).findByUserId(userId);
        verify(beneficiaryRepository)
                .findByCustomerId(customer.getId());
    }

    @Test
    void shouldGetBeneficiaryBelongingToCurrentUser() {

        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Beneficiary beneficiary = createBeneficiary(
                1L,
                customer,
                "987654321098",
                "Second User"
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(beneficiaryRepository.findById(1L))
                .thenReturn(Optional.of(beneficiary));

        BeneficiaryResponse response =
                beneficiaryService.getBeneficiary(
                        userId,
                        1L
                );

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(
                "987654321098",
                response.getBeneficiaryAccountNumber()
        );

        verify(beneficiaryRepository).findById(1L);
    }

    @Test
    void shouldRejectAccessToAnotherUsersBeneficiary() {

        Long currentUserId = 1L;

        Customer currentCustomer = new Customer();
        currentCustomer.setId(1L);
        currentCustomer.setUserId(currentUserId);

        Customer anotherCustomer = new Customer();
        anotherCustomer.setId(2L);
        anotherCustomer.setUserId(3L);

        Beneficiary anotherUsersBeneficiary =
                createBeneficiary(
                        2L,
                        anotherCustomer,
                        "987654321098",
                        "User 2"
                );

        when(customerRepository.findByUserId(currentUserId))
                .thenReturn(Optional.of(currentCustomer));

        when(beneficiaryRepository.findById(2L))
                .thenReturn(Optional.of(anotherUsersBeneficiary));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> beneficiaryService.getBeneficiary(
                        currentUserId,
                        2L
                )
        );

        assertEquals(
                "You do not have access to this beneficiary",
                exception.getMessage()
        );
    }

    @Test
    void shouldDeleteBeneficiaryBelongingToCurrentUser() {

        Long userId = 1L;

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setUserId(userId);

        Beneficiary beneficiary = createBeneficiary(
                1L,
                customer,
                "987654321098",
                "Second User"
        );

        when(customerRepository.findByUserId(userId))
                .thenReturn(Optional.of(customer));

        when(beneficiaryRepository.findById(1L))
                .thenReturn(Optional.of(beneficiary));

        beneficiaryService.deleteBeneficiary(
                userId,
                1L
        );

        verify(beneficiaryRepository).delete(beneficiary);
    }

    @Test
    void shouldRejectDeletingAnotherUsersBeneficiary() {

        Long currentUserId = 1L;

        Customer currentCustomer = new Customer();
        currentCustomer.setId(1L);
        currentCustomer.setUserId(currentUserId);

        Customer anotherCustomer = new Customer();
        anotherCustomer.setId(2L);
        anotherCustomer.setUserId(3L);

        Beneficiary anotherUsersBeneficiary =
                createBeneficiary(
                        2L,
                        anotherCustomer,
                        "987654321098",
                        "User 2"
                );

        when(customerRepository.findByUserId(currentUserId))
                .thenReturn(Optional.of(currentCustomer));

        when(beneficiaryRepository.findById(2L))
                .thenReturn(Optional.of(anotherUsersBeneficiary));

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> beneficiaryService.deleteBeneficiary(
                        currentUserId,
                        2L
                )
        );

        assertEquals(
                "You do not have access to this beneficiary",
                exception.getMessage()
        );

        verify(beneficiaryRepository, never())
                .delete(any(Beneficiary.class));
    }

    private Account createAccount(
            Long id,
            Customer customer,
            String accountNumber
    ) {

        Account account = new Account();

        account.setId(id);
        account.setCustomer(customer);
        account.setAccountNumber(accountNumber);
        account.setAccountType(AccountType.SAVINGS);
        account.setBalance(java.math.BigDecimal.ZERO);
        account.setCurrency("INR");
        account.setStatus(AccountStatus.ACTIVE);

        return account;
    }

    private Beneficiary createBeneficiary(
            Long id,
            Customer customer,
            String accountNumber,
            String nickname
    ) {

        Beneficiary beneficiary = new Beneficiary();

        beneficiary.setId(id);
        beneficiary.setCustomer(customer);
        beneficiary.setBeneficiaryAccountNumber(accountNumber);
        beneficiary.setNickname(nickname);

        return beneficiary;
    }
}