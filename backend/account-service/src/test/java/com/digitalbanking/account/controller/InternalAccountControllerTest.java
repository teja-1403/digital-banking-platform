package com.digitalbanking.account.controller;

import com.digitalbanking.account.dto.AccountOwnershipResponse;
import com.digitalbanking.account.exception.BusinessRuleException;
import com.digitalbanking.account.exception.GlobalExceptionHandler;
import com.digitalbanking.account.exception.ResourceNotFoundException;
import com.digitalbanking.account.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InternalAccountController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class InternalAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Test
    void executeTransfer_shouldReturn204()
            throws Exception {

        doNothing().when(accountService)
                .executeInternalTransfer(
                        42L,
                        10L,
                        20L,
                        new java.math.BigDecimal("1000.00")
                );

        String requestBody = """
                {
                    "sourceAccountId": 10,
                    "destinationAccountId": 20,
                    "amount": 1000.00,
                    "userId": 42
                }
                """;

        mockMvc.perform(
                        post("/internal/accounts/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(accountService)
                .executeInternalTransfer(
                        42L,
                        10L,
                        20L,
                        new java.math.BigDecimal("1000.00")
                );
    }

    @Test
    void executeTransfer_shouldReturn400ForMissingRequiredFields()
            throws Exception {

        String requestBody = """
                {
                }
                """;

        mockMvc.perform(
                        post("/internal/accounts/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountService);
    }

    @Test
    void executeTransfer_shouldReturn400ForInvalidAmount()
            throws Exception {

        String requestBody = """
                {
                    "sourceAccountId": 10,
                    "destinationAccountId": 20,
                    "amount": 0,
                    "userId": 42
                }
                """;

        mockMvc.perform(
                        post("/internal/accounts/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountService);
    }

    @Test
    void executeTransfer_shouldReturn400ForMoreThanTwoDecimalPlaces()
            throws Exception {

        String requestBody = """
                {
                    "sourceAccountId": 10,
                    "destinationAccountId": 20,
                    "amount": 100.123,
                    "userId": 42
                }
                """;

        mockMvc.perform(
                        post("/internal/accounts/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountService);
    }

    @Test
    void executeTransfer_shouldMapBusinessRuleExceptionTo400()
            throws Exception {

        doThrow(
                new BusinessRuleException(
                        "Insufficient balance"
                )
        ).when(accountService)
                .executeInternalTransfer(
                        42L,
                        10L,
                        20L,
                        new java.math.BigDecimal("1000.00")
                );

        String requestBody = """
                {
                    "sourceAccountId": 10,
                    "destinationAccountId": 20,
                    "amount": 1000.00,
                    "userId": 42
                }
                """;

        mockMvc.perform(
                        post("/internal/accounts/transfer")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.error")
                                .value("BUSINESS_RULE_VIOLATION")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("Insufficient balance")
                );

        verify(accountService)
                .executeInternalTransfer(
                        42L,
                        10L,
                        20L,
                        new java.math.BigDecimal("1000.00")
                );
    }

    @Test
    void checkOwnership_shouldReturnTrueWhenUserOwnsAccount()
            throws Exception {

        when(
                accountService.isAccountOwnedByUser(
                        42L,
                        10L
                )
        ).thenReturn(true);

        mockMvc.perform(
                        get("/internal/accounts/{accountId}/ownership", 10L)
                                .param("userId", "42")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.accountId")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.owner")
                                .value(true)
                );

        verify(accountService)
                .isAccountOwnedByUser(42L, 10L);
    }

    @Test
    void checkOwnership_shouldReturnFalseWhenUserDoesNotOwnAccount()
            throws Exception {

        when(
                accountService.isAccountOwnedByUser(
                        42L,
                        10L
                )
        ).thenReturn(false);

        mockMvc.perform(
                        get("/internal/accounts/{accountId}/ownership", 10L)
                                .param("userId", "42")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.accountId")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.owner")
                                .value(false)
                );

        verify(accountService)
                .isAccountOwnedByUser(42L, 10L);
    }

    @Test
    void checkOwnership_shouldReturn400WhenUserIdIsMissing()
            throws Exception {

        mockMvc.perform(
                        get(
                                "/internal/accounts/{accountId}/ownership",
                                10L
                        )
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountService);
    }

    @Test
    void checkOwnership_shouldMapResourceNotFoundTo404()
            throws Exception {

        when(
                accountService.isAccountOwnedByUser(
                        42L,
                        99L
                )
        ).thenThrow(
                new ResourceNotFoundException(
                        "Account not found"
                )
        );

        mockMvc.perform(
                        get(
                                "/internal/accounts/{accountId}/ownership",
                                99L
                        )
                                .param("userId", "42")
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value("Account not found")
                );
    }
}