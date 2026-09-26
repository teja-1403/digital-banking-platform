package com.digitalbanking.transaction.controller;

import com.digitalbanking.transaction.dto.AdminTransactionStatsResponse;
import com.digitalbanking.transaction.entity.TransactionStatus;
import com.digitalbanking.transaction.repository.TransactionRepository;
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
    private TransactionRepository transactionRepository;

    @Test
    void getTransactionStats_shouldReturn200WithStatistics()
            throws Exception {

        when(transactionRepository.count())
                .thenReturn(100L);

        when(transactionRepository.countByStatus(
                TransactionStatus.COMPLETED
        )).thenReturn(80L);

        when(transactionRepository.countByStatus(
                TransactionStatus.FAILED
        )).thenReturn(20L);

        when(transactionRepository.getCompletedTransactionVolume())
                .thenReturn(new BigDecimal("125000.50"));

        mockMvc.perform(
                        get("/api/admin/transaction-stats")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.totalTransactions")
                                .value(100)
                )
                .andExpect(
                        jsonPath("$.completedTransactions")
                                .value(80)
                )
                .andExpect(
                        jsonPath("$.failedTransactions")
                                .value(20)
                )
                .andExpect(
                        jsonPath("$.totalTransactionVolume")
                                .value(125000.50)
                );

        verify(transactionRepository)
                .count();

        verify(transactionRepository)
                .countByStatus(TransactionStatus.COMPLETED);

        verify(transactionRepository)
                .countByStatus(TransactionStatus.FAILED);

        verify(transactionRepository)
                .getCompletedTransactionVolume();
    }

    @Test
    void getTransactionStats_shouldReturnZeroStatistics()
            throws Exception {

        when(transactionRepository.count())
                .thenReturn(0L);

        when(transactionRepository.countByStatus(
                TransactionStatus.COMPLETED
        )).thenReturn(0L);

        when(transactionRepository.countByStatus(
                TransactionStatus.FAILED
        )).thenReturn(0L);

        when(transactionRepository.getCompletedTransactionVolume())
                .thenReturn(BigDecimal.ZERO);

        mockMvc.perform(
                        get("/api/admin/transaction-stats")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.totalTransactions")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.completedTransactions")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.failedTransactions")
                                .value(0)
                )
                .andExpect(
                        jsonPath("$.totalTransactionVolume")
                                .value(0)
                );
    }

    @Test
    void getTransactionStats_shouldUseCorrectStatusFilters()
            throws Exception {

        when(transactionRepository.count())
                .thenReturn(50L);

        when(transactionRepository.countByStatus(
                TransactionStatus.COMPLETED
        )).thenReturn(35L);

        when(transactionRepository.countByStatus(
                TransactionStatus.FAILED
        )).thenReturn(15L);

        when(transactionRepository.getCompletedTransactionVolume())
                .thenReturn(new BigDecimal("50000.00"));

        mockMvc.perform(
                        get("/api/admin/transaction-stats")
                )
                .andExpect(status().isOk());

        verify(transactionRepository, times(1))
                .countByStatus(TransactionStatus.COMPLETED);

        verify(transactionRepository, times(1))
                .countByStatus(TransactionStatus.FAILED);

        verify(
                transactionRepository,
                never()
        ).countByStatus(TransactionStatus.PENDING);
    }

    @Test
    void getTransactionStats_shouldNotCallRepositoryMoreThanRequired()
            throws Exception {

        when(transactionRepository.count())
                .thenReturn(10L);

        when(transactionRepository.countByStatus(
                TransactionStatus.COMPLETED
        )).thenReturn(7L);

        when(transactionRepository.countByStatus(
                TransactionStatus.FAILED
        )).thenReturn(3L);

        when(transactionRepository.getCompletedTransactionVolume())
                .thenReturn(new BigDecimal("7000.00"));

        mockMvc.perform(
                        get("/api/admin/transaction-stats")
                )
                .andExpect(status().isOk());

        verify(transactionRepository, times(1))
                .count();

        verify(transactionRepository, times(1))
                .countByStatus(TransactionStatus.COMPLETED);

        verify(transactionRepository, times(1))
                .countByStatus(TransactionStatus.FAILED);

        verify(transactionRepository, times(1))
                .getCompletedTransactionVolume();

        verifyNoMoreInteractions(transactionRepository);
    }
}