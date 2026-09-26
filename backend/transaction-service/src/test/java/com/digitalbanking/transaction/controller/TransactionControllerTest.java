package com.digitalbanking.transaction.controller;

import com.digitalbanking.transaction.dto.TransactionHistoryResponse;
import com.digitalbanking.transaction.dto.TransactionResponse;
import com.digitalbanking.transaction.dto.TransferRequest;
import com.digitalbanking.transaction.entity.TransactionStatus;
import com.digitalbanking.transaction.entity.TransactionType;
import com.digitalbanking.transaction.exception.BusinessRuleException;
import com.digitalbanking.transaction.exception.GlobalExceptionHandler;
import com.digitalbanking.transaction.service.TransactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        GlobalExceptionHandler.class,
        TransactionControllerTest.SecurityTestConfig.class
})
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticateUser(Long userId) {

        Jwt jwt = Jwt.withTokenValue("test-access-token")
                .header("alg", "none")
                .claim("userId", userId)
                .build();

        JwtAuthenticationToken authentication =
                new JwtAuthenticationToken(jwt);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    @Test
    void transfer_shouldReturn200AndTransactionResponse()
            throws Exception {

        authenticateUser(42L);

        TransactionResponse response =
                new TransactionResponse(
                        1L,
                        "TXN-20260926-ABC12345",
                        "idem-001",
                        TransactionType.TRANSFER,
                        TransactionStatus.COMPLETED,
                        1L,
                        3L,
                        new BigDecimal("100.00"),
                        "INR",
                        "Test transfer",
                        LocalDateTime.of(
                                2026,
                                9,
                                26,
                                12,
                                0
                        ),
                        LocalDateTime.of(
                                2026,
                                9,
                                26,
                                12,
                                0
                        )
                );

        when(
                transactionService.initiateTransfer(
                        eq(42L),
                        eq("idem-001"),
                        any(TransferRequest.class)
                )
        ).thenReturn(response);

        String requestBody = """
                {
                    "sourceAccountId": 1,
                    "destinationAccountId": 3,
                    "amount": 100.00,
                    "currency": "INR",
                    "description": "Test transfer"
                }
                """;

        mockMvc.perform(
                        post("/api/transactions/transfers")
                                .header(
                                        "Idempotency-Key",
                                        "idem-001"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id").value(1)
                )
                .andExpect(
                        jsonPath("$.transactionReference")
                                .value("TXN-20260926-ABC12345")
                )
                .andExpect(
                        jsonPath("$.idempotencyKey")
                                .value("idem-001")
                )
                .andExpect(
                        jsonPath("$.type")
                                .value("TRANSFER")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("COMPLETED")
                )
                .andExpect(
                        jsonPath("$.sourceAccountId")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.destinationAccountId")
                                .value(3)
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(100.00)
                )
                .andExpect(
                        jsonPath("$.currency")
                                .value("INR")
                )
                .andExpect(
                        jsonPath("$.description")
                                .value("Test transfer")
                );

        verify(transactionService)
                .initiateTransfer(
                        eq(42L),
                        eq("idem-001"),
                        any(TransferRequest.class)
                );
    }

    @Test
    void transfer_shouldReturn400WhenIdempotencyKeyIsMissing()
            throws Exception {

        authenticateUser(42L);

        String requestBody = """
                {
                    "sourceAccountId": 1,
                    "destinationAccountId": 3,
                    "amount": 100.00,
                    "currency": "INR",
                    "description": "Test transfer"
                }
                """;

        mockMvc.perform(
                        post("/api/transactions/transfers")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transactionService);
    }

    @Test
    void transfer_shouldReturn400ForInvalidRequest()
            throws Exception {

        authenticateUser(42L);

        String requestBody = """
                {
                    "sourceAccountId": 1,
                    "destinationAccountId": 3,
                    "amount": 0,
                    "currency": "",
                    "description": "Test transfer"
                }
                """;

        mockMvc.perform(
                        post("/api/transactions/transfers")
                                .header(
                                        "Idempotency-Key",
                                        "idem-invalid"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transactionService);
    }

    @Test
    void transfer_shouldMapBusinessRuleExceptionTo400()
            throws Exception {

        authenticateUser(42L);

        when(
                transactionService.initiateTransfer(
                        eq(42L),
                        eq("idem-business"),
                        any(TransferRequest.class)
                )
        ).thenThrow(
                new BusinessRuleException(
                        "Transfer amount must be greater than zero"
                )
        );

        String requestBody = """
                {
                    "sourceAccountId": 1,
                    "destinationAccountId": 3,
                    "amount": 100.00,
                    "currency": "INR",
                    "description": "Test transfer"
                }
                """;

        mockMvc.perform(
                        post("/api/transactions/transfers")
                                .header(
                                        "Idempotency-Key",
                                        "idem-business"
                                )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.error")
                                .value("BUSINESS_RULE_VIOLATION")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Transfer amount must be greater than zero"
                                )
                );

        verify(transactionService)
                .initiateTransfer(
                        eq(42L),
                        eq("idem-business"),
                        any(TransferRequest.class)
                );
    }

    @Test
    void getTransactionDetails_shouldReturn200()
            throws Exception {

        authenticateUser(42L);

        TransactionResponse response =
                new TransactionResponse(
                        5L,
                        "TXN-DETAIL-001",
                        "idem-detail-001",
                        TransactionType.TRANSFER,
                        TransactionStatus.COMPLETED,
                        1L,
                        3L,
                        new BigDecimal("250.00"),
                        "INR",
                        "Payment",
                        LocalDateTime.of(
                                2026,
                                9,
                                26,
                                10,
                                0
                        ),
                        LocalDateTime.of(
                                2026,
                                9,
                                26,
                                10,
                                1
                        )
                );

        when(
                transactionService.getTransactionDetails(
                        42L,
                        "TXN-DETAIL-001"
                )
        ).thenReturn(response);

        mockMvc.perform(
                        get(
                                "/api/transactions/{transactionReference}",
                                "TXN-DETAIL-001"
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id").value(5)
                )
                .andExpect(
                        jsonPath("$.transactionReference")
                                .value("TXN-DETAIL-001")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("COMPLETED")
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(250.00)
                )
                .andExpect(
                        jsonPath("$.currency")
                                .value("INR")
                );

        verify(transactionService)
                .getTransactionDetails(
                        42L,
                        "TXN-DETAIL-001"
                );
    }

    @Test
    void getAccountHistory_shouldReturn200AndTransactions()
            throws Exception {

        authenticateUser(42L);

        TransactionHistoryResponse first =
                new TransactionHistoryResponse(
                        "TXN-HISTORY-001",
                        TransactionType.TRANSFER,
                        TransactionStatus.COMPLETED,
                        1L,
                        3L,
                        new BigDecimal("100.00"),
                        "INR",
                        "Payment 1",
                        LocalDateTime.of(
                                2026,
                                9,
                                26,
                                11,
                                0
                        ),
                        LocalDateTime.of(
                                2026,
                                9,
                                26,
                                11,
                                1
                        )
                );

        TransactionHistoryResponse second =
                new TransactionHistoryResponse(
                        "TXN-HISTORY-002",
                        TransactionType.TRANSFER,
                        TransactionStatus.FAILED,
                        4L,
                        1L,
                        new BigDecimal("50.00"),
                        "INR",
                        "Payment 2",
                        LocalDateTime.of(
                                2026,
                                9,
                                25,
                                15,
                                0
                        ),
                        null
                );

        when(
                transactionService.getAccountHistory(
                        42L,
                        1L
                )
        ).thenReturn(
                List.of(first, second)
        );

        mockMvc.perform(
                        get(
                                "/api/transactions/account/{accountId}",
                                1L
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()").value(2)
                )
                .andExpect(
                        jsonPath("$[0].transactionReference")
                                .value("TXN-HISTORY-001")
                )
                .andExpect(
                        jsonPath("$[0].status")
                                .value("COMPLETED")
                )
                .andExpect(
                        jsonPath("$[1].transactionReference")
                                .value("TXN-HISTORY-002")
                )
                .andExpect(
                        jsonPath("$[1].status")
                                .value("FAILED")
                );

        verify(transactionService)
                .getAccountHistory(
                        42L,
                        1L
                );
    }

    @Test
    void getAccountHistory_shouldReturnEmptyListWhenNoTransactions()
            throws Exception {

        authenticateUser(42L);

        when(
                transactionService.getAccountHistory(
                        42L,
                        1L
                )
        ).thenReturn(
                List.of()
        );

        mockMvc.perform(
                        get(
                                "/api/transactions/account/{accountId}",
                                1L
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()").value(0)
                );

        verify(transactionService)
                .getAccountHistory(
                        42L,
                        1L
                );
    }

    @TestConfiguration
    static class SecurityTestConfig
            implements WebMvcConfigurer {

        @Override
        public void addArgumentResolvers(
                List<HandlerMethodArgumentResolver> resolvers
        ) {
            resolvers.add(
                    new AuthenticationPrincipalArgumentResolver()
            );
        }
    }
}