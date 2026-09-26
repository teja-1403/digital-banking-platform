package com.digitalbanking.account.controller;

import com.digitalbanking.account.entity.AccountStatus;
import com.digitalbanking.account.repository.AccountRepository;
import com.digitalbanking.account.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private AccountRepository accountRepository;

    @Test
    void getAccountStats_shouldReturn200WithStatistics()
            throws Exception {

        when(customerRepository.count())
                .thenReturn(100L);

        when(accountRepository.count())
                .thenReturn(150L);

        when(accountRepository.countByStatus(
                AccountStatus.ACTIVE
        )).thenReturn(120L);

        when(accountRepository.getTotalActiveBalance())
                .thenReturn(new BigDecimal("1250000.50"));

        mockMvc.perform(
                        get("/api/admin/account-stats")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.totalCustomers")
                                .value(100)
                )
                .andExpect(
                        jsonPath("$.totalAccounts")
                                .value(150)
                )
                .andExpect(
                        jsonPath("$.activeAccounts")
                                .value(120)
                )
                .andExpect(
                        jsonPath("$.totalBalance")
                                .value(1250000.50)
                );

        verify(customerRepository)
                .count();

        verify(accountRepository)
                .count();

        verify(accountRepository)
                .countByStatus(AccountStatus.ACTIVE);

        verify(accountRepository)
                .getTotalActiveBalance();
    }

    @Test
    void getAccountStats_shouldReturnZeroStatistics()
            throws Exception {

        when(customerRepository.count())
                .thenReturn(0L);

        when(accountRepository.count())
                .thenReturn(0L);

        when(accountRepository.countByStatus(
                AccountStatus.ACTIVE
        )).thenReturn(0L);

        when(accountRepository.getTotalActiveBalance())
                .thenReturn(BigDecimal.ZERO);

        mockMvc.perform(
                        get("/api/admin/account-stats")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.totalCustomers")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.totalAccounts")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.activeAccounts")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.totalBalance")
                                .value(0)
                );
    }

    @Test
    void getAccountStats_shouldUseActiveStatusFilter()
            throws Exception {

        when(customerRepository.count())
                .thenReturn(20L);

        when(accountRepository.count())
                .thenReturn(30L);

        when(accountRepository.countByStatus(
                AccountStatus.ACTIVE
        )).thenReturn(25L);

        when(accountRepository.getTotalActiveBalance())
                .thenReturn(new BigDecimal("50000.00"));

        mockMvc.perform(
                        get("/api/admin/account-stats")
                )
                .andExpect(status().isOk());

        verify(accountRepository, times(1))
                .countByStatus(AccountStatus.ACTIVE);

        verify(
                accountRepository,
                never()
        ).countByStatus(AccountStatus.BLOCKED);

        verify(
                accountRepository,
                never()
        ).countByStatus(AccountStatus.CLOSED);
    }

    @Test
    void getAccountStats_shouldCallEachRepositoryOperationOnce()
            throws Exception {

        when(customerRepository.count())
                .thenReturn(10L);

        when(accountRepository.count())
                .thenReturn(15L);

        when(accountRepository.countByStatus(
                AccountStatus.ACTIVE
        )).thenReturn(12L);

        when(accountRepository.getTotalActiveBalance())
                .thenReturn(new BigDecimal("25000.00"));

        mockMvc.perform(
                        get("/api/admin/account-stats")
                )
                .andExpect(status().isOk());

        verify(customerRepository, times(1))
                .count();

        verify(accountRepository, times(1))
                .count();

        verify(accountRepository, times(1))
                .countByStatus(AccountStatus.ACTIVE);

        verify(accountRepository, times(1))
                .getTotalActiveBalance();

        verifyNoMoreInteractions(
                customerRepository,
                accountRepository
        );
    }
}