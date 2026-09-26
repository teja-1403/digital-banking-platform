package com.digitalbanking.account.controller;

import com.digitalbanking.account.dto.AccountResponse;
import com.digitalbanking.account.dto.FundAccountResponse;
import com.digitalbanking.account.entity.AccountStatus;
import com.digitalbanking.account.entity.AccountType;
import com.digitalbanking.account.exception.BusinessRuleException;
import com.digitalbanking.account.exception.GlobalExceptionHandler;
import com.digitalbanking.account.security.AuthenticatedUser;
import com.digitalbanking.account.service.AccountService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        GlobalExceptionHandler.class,
        AccountControllerTest.SecurityTestConfig.class
})
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private AuthenticatedUser authenticatedUser;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    private Jwt authenticateUser(Long userId) {

        Jwt jwt = Jwt.withTokenValue("test-access-token")
                .header("alg", "none")
                .subject("test-user")
                .claim("userId", userId)
                .build();

        JwtAuthenticationToken authentication =
                new JwtAuthenticationToken(jwt);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        when(authenticatedUser.getUserId(jwt))
                .thenReturn(userId);

        return jwt;
    }

    private AccountResponse accountResponse(
            Long id,
            String accountNumber,
            AccountType accountType,
            BigDecimal balance,
            AccountStatus status
    ) {
        return new AccountResponse(
                id,
                accountNumber,
                accountType,
                balance,
                "INR",
                status
        );
    }

    @Test
    void createAccount_shouldReturn201() throws Exception {

        Jwt jwt = authenticateUser(42L);

        AccountResponse response =
                accountResponse(
                        1L,
                        "123456789012",
                        AccountType.SAVINGS,
                        new BigDecimal("0.00"),
                        AccountStatus.ACTIVE
                );

        when(
                accountService.createAccount(
                        eq(42L),
                        any()
                )
        ).thenReturn(response);

        String requestBody = """
                {
                    "accountType": "SAVINGS"
                }
                """;

        mockMvc.perform(
                        post("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(
                        jsonPath("$.accountNumber")
                                .value("123456789012")
                )
                .andExpect(
                        jsonPath("$.accountType")
                                .value("SAVINGS")
                )
                .andExpect(
                        jsonPath("$.balance")
                                .value(0.00)
                )
                .andExpect(
                        jsonPath("$.currency")
                                .value("INR")
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE")
                );

        verify(authenticatedUser)
                .getUserId(jwt);

        verify(accountService)
                .createAccount(
                        eq(42L),
                        any()
                );
    }

    @Test
    void createAccount_shouldReturn400ForInvalidRequest()
            throws Exception {

        authenticateUser(42L);

        String requestBody = """
                {
                }
                """;

        mockMvc.perform(
                        post("/api/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountService);
    }

    @Test
    void getCurrentUserAccounts_shouldReturnAccounts()
            throws Exception {

        Jwt jwt = authenticateUser(42L);

        List<AccountResponse> responses =
                List.of(
                        accountResponse(
                                1L,
                                "111111111111",
                                AccountType.SAVINGS,
                                new BigDecimal("5000.00"),
                                AccountStatus.ACTIVE
                        ),
                        accountResponse(
                                2L,
                                "222222222222",
                                AccountType.CURRENT,
                                new BigDecimal("2500.00"),
                                AccountStatus.ACTIVE
                        )
                );

        when(
                accountService.getCurrentUserAccounts(42L)
        ).thenReturn(responses);

        mockMvc.perform(
                        get("/api/accounts")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].accountNumber")
                                .value("111111111111")
                )
                .andExpect(
                        jsonPath("$[1].id")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[1].accountNumber")
                                .value("222222222222")
                );

        verify(authenticatedUser)
                .getUserId(jwt);

        verify(accountService)
                .getCurrentUserAccounts(42L);
    }

    @Test
    void getCurrentUserAccounts_shouldReturnEmptyList()
            throws Exception {

        Jwt jwt = authenticateUser(42L);

        when(
                accountService.getCurrentUserAccounts(42L)
        ).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/accounts")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(0)
                );

        verify(authenticatedUser)
                .getUserId(jwt);

        verify(accountService)
                .getCurrentUserAccounts(42L);
    }

    @Test
    void getAccount_shouldReturnAccount()
            throws Exception {

        Jwt jwt = authenticateUser(42L);

        AccountResponse response =
                accountResponse(
                        10L,
                        "123456789012",
                        AccountType.SAVINGS,
                        new BigDecimal("15000.00"),
                        AccountStatus.ACTIVE
                );

        when(
                accountService.getAccount(
                        42L,
                        10L
                )
        ).thenReturn(response);

        mockMvc.perform(
                        get("/api/accounts/{accountId}", 10L)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.accountNumber")
                                .value("123456789012")
                )
                .andExpect(
                        jsonPath("$.accountType")
                                .value("SAVINGS")
                )
                .andExpect(
                        jsonPath("$.balance")
                                .value(15000.00)
                )
                .andExpect(
                        jsonPath("$.status")
                                .value("ACTIVE")
                );

        verify(accountService)
                .getAccount(42L, 10L);
    }

    @Test
    void fundAccount_shouldReturn200()
            throws Exception {

        Jwt jwt = authenticateUser(42L);

        FundAccountResponse response =
                new FundAccountResponse(
                        "FND-TEST-001",
                        10L,
                        "123456789012",
                        new BigDecimal("10000.00"),
                        new BigDecimal("15000.00"),
                        "INR"
                );

        when(
                accountService.fundAccount(
                        eq(42L),
                        eq(10L),
                        eq(new BigDecimal("10000.00"))
                )
        ).thenReturn(response);

        String requestBody = """
                {
                    "amount": 10000.00
                }
                """;

        mockMvc.perform(
                        post("/api/accounts/{accountId}/fund", 10L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.fundingReference")
                                .value("FND-TEST-001")
                )
                .andExpect(
                        jsonPath("$.accountId")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.accountNumber")
                                .value("123456789012")
                )
                .andExpect(
                        jsonPath("$.amount")
                                .value(10000.00)
                )
                .andExpect(
                        jsonPath("$.balance")
                                .value(15000.00)
                )
                .andExpect(
                        jsonPath("$.currency")
                                .value("INR")
                );

        verify(authenticatedUser)
                .getUserId(jwt);

        verify(accountService)
                .fundAccount(
                        42L,
                        10L,
                        new BigDecimal("10000.00")
                );
    }

    @Test
    void fundAccount_shouldReturn400ForInvalidAmount()
            throws Exception {

        authenticateUser(42L);

        String requestBody = """
                {
                    "amount": 0
                }
                """;

        mockMvc.perform(
                        post("/api/accounts/{accountId}/fund", 10L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(accountService);
    }

    @Test
    void getFundingStatus_shouldReturnTrue()
            throws Exception {

        Jwt jwt = authenticateUser(42L);

        when(
                accountService.hasFundingForUser(42L)
        ).thenReturn(true);

        mockMvc.perform(
                        get("/api/accounts/funding-status")
                )
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        verify(authenticatedUser)
                .getUserId(jwt);

        verify(accountService)
                .hasFundingForUser(42L);
    }

    @Test
    void freezeAccount_shouldReturnBlockedAccount()
            throws Exception {

        Jwt jwt = authenticateUser(42L);

        AccountResponse response =
                accountResponse(
                        10L,
                        "123456789012",
                        AccountType.SAVINGS,
                        new BigDecimal("5000.00"),
                        AccountStatus.BLOCKED
                );

        when(
                accountService.freezeAccount(42L, 10L)
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/accounts/{accountId}/freeze", 10L)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id").value(10)
                )
                .andExpect(
                        jsonPath("$.status").value("BLOCKED")
                );

        verify(authenticatedUser)
                .getUserId(jwt);

        verify(accountService)
                .freezeAccount(42L, 10L);
    }

    @Test
    void activateAccount_shouldReturnActiveAccount()
            throws Exception {

        Jwt jwt = authenticateUser(42L);

        AccountResponse response =
                accountResponse(
                        10L,
                        "123456789012",
                        AccountType.SAVINGS,
                        new BigDecimal("5000.00"),
                        AccountStatus.ACTIVE
                );

        when(
                accountService.activateAccount(42L, 10L)
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/accounts/{accountId}/activate", 10L)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id").value(10)
                )
                .andExpect(
                        jsonPath("$.status").value("ACTIVE")
                );

        verify(authenticatedUser)
                .getUserId(jwt);

        verify(accountService)
                .activateAccount(42L, 10L);
    }

    @Test
    void closeAccount_shouldReturnClosedAccount()
            throws Exception {

        Jwt jwt = authenticateUser(42L);

        AccountResponse response =
                accountResponse(
                        10L,
                        "123456789012",
                        AccountType.SAVINGS,
                        new BigDecimal("0.00"),
                        AccountStatus.CLOSED
                );

        when(
                accountService.closeAccount(42L, 10L)
        ).thenReturn(response);

        mockMvc.perform(
                        post("/api/accounts/{accountId}/close", 10L)
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id").value(10)
                )
                .andExpect(
                        jsonPath("$.status").value("CLOSED")
                );

        verify(authenticatedUser)
                .getUserId(jwt);

        verify(accountService)
                .closeAccount(42L, 10L);
    }

    @Test
    void freezeAccount_shouldMapBusinessRuleExceptionTo400()
            throws Exception {

        authenticateUser(42L);

        when(
                accountService.freezeAccount(42L, 10L)
        ).thenThrow(
                new BusinessRuleException(
                        "Account is already frozen"
                )
        );

        mockMvc.perform(
                        post("/api/accounts/{accountId}/freeze", 10L)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.error")
                                .value("BUSINESS_RULE_VIOLATION")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value("Account is already frozen")
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