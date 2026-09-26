package com.digitalbanking.account.controller;

import com.digitalbanking.account.dto.BeneficiaryResponse;
import com.digitalbanking.account.exception.BusinessRuleException;
import com.digitalbanking.account.exception.GlobalExceptionHandler;
import com.digitalbanking.account.exception.ResourceNotFoundException;
import com.digitalbanking.account.security.AuthenticatedUser;
import com.digitalbanking.account.service.BeneficiaryService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeneficiaryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        GlobalExceptionHandler.class,
        BeneficiaryControllerTest.SecurityTestConfig.class
})
class BeneficiaryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BeneficiaryService beneficiaryService;

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

    private BeneficiaryResponse beneficiaryResponse(
            Long id,
            Long accountId,
            String accountNumber,
            String nickname
    ) {
        return new BeneficiaryResponse(
                id,
                accountId,
                accountNumber,
                nickname,
                LocalDateTime.of(
                        2026,
                        9,
                        26,
                        10,
                        0
                )
        );
    }

    @Test
    void createBeneficiary_shouldReturn201()
            throws Exception {

        Jwt jwt = authenticateUser(42L);

        BeneficiaryResponse response =
                beneficiaryResponse(
                        1L,
                        10L,
                        "123456789012",
                        "Primary Account"
                );

        when(
                beneficiaryService.createBeneficiary(
                        eq(42L),
                        any()
                )
        ).thenReturn(response);

        String requestBody = """
                {
                    "beneficiaryAccountNumber": "123456789012",
                    "nickname": "Primary Account"
                }
                """;

        mockMvc.perform(
                        post("/api/beneficiaries")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.beneficiaryAccountId")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.beneficiaryAccountNumber")
                                .value("123456789012")
                )
                .andExpect(
                        jsonPath("$.nickname")
                                .value("Primary Account")
                );

        verify(authenticatedUser)
                .getUserId(jwt);

        verify(beneficiaryService)
                .createBeneficiary(
                        eq(42L),
                        any()
                );
    }

    @Test
    void createBeneficiary_shouldAllowMissingOptionalNickname()
            throws Exception {

        Jwt jwt = authenticateUser(42L);

        BeneficiaryResponse response =
                beneficiaryResponse(
                        1L,
                        10L,
                        "123456789012",
                        null
                );

        when(
                beneficiaryService.createBeneficiary(
                        eq(42L),
                        any()
                )
        ).thenReturn(response);

        String requestBody = """
                {
                    "beneficiaryAccountNumber": "123456789012"
                }
                """;

        mockMvc.perform(
                        post("/api/beneficiaries")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.beneficiaryAccountNumber")
                                .value("123456789012")
                )
                .andExpect(
                        jsonPath("$.nickname")
                                .doesNotExist()
                );

        verify(authenticatedUser)
                .getUserId(jwt);

        verify(beneficiaryService)
                .createBeneficiary(
                        eq(42L),
                        any()
                );
    }

    @Test
    void createBeneficiary_shouldReturn400ForMissingAccountNumber()
            throws Exception {

        authenticateUser(42L);

        String requestBody = """
                {
                    "nickname": "Primary Account"
                }
                """;

        mockMvc.perform(
                        post("/api/beneficiaries")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(beneficiaryService);
    }

    @Test
    void getCurrentUserBeneficiaries_shouldReturnList()
            throws Exception {

        Jwt jwt = authenticateUser(42L);

        List<BeneficiaryResponse> responses =
                List.of(
                        beneficiaryResponse(
                                1L,
                                10L,
                                "123456789012",
                                "Primary Account"
                        ),
                        beneficiaryResponse(
                                2L,
                                20L,
                                "987654321098",
                                "Savings Account"
                        )
                );

        when(
                beneficiaryService
                        .getCurrentUserBeneficiaries(42L)
        ).thenReturn(responses);

        mockMvc.perform(
                        get("/api/beneficiaries")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].beneficiaryAccountNumber")
                                .value("123456789012")
                )
                .andExpect(
                        jsonPath("$[0].nickname")
                                .value("Primary Account")
                )
                .andExpect(
                        jsonPath("$[1].id")
                                .value(2)
                )
                .andExpect(
                        jsonPath("$[1].beneficiaryAccountNumber")
                                .value("987654321098")
                );

        verify(authenticatedUser)
                .getUserId(jwt);

        verify(beneficiaryService)
                .getCurrentUserBeneficiaries(42L);
    }

    @Test
    void getCurrentUserBeneficiaries_shouldReturnEmptyList()
            throws Exception {

        Jwt jwt = authenticateUser(42L);

        when(
                beneficiaryService
                        .getCurrentUserBeneficiaries(42L)
        ).thenReturn(List.of());

        mockMvc.perform(
                        get("/api/beneficiaries")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.length()")
                                .value(0)
                );

        verify(authenticatedUser)
                .getUserId(jwt);

        verify(beneficiaryService)
                .getCurrentUserBeneficiaries(42L);
    }

    @Test
    void getBeneficiary_shouldReturn200()
            throws Exception {

        Jwt jwt = authenticateUser(42L);

        BeneficiaryResponse response =
                beneficiaryResponse(
                        5L,
                        15L,
                        "555555555555",
                        "Friend"
                );

        when(
                beneficiaryService.getBeneficiary(
                        42L,
                        5L
                )
        ).thenReturn(response);

        mockMvc.perform(
                        get(
                                "/api/beneficiaries/{beneficiaryId}",
                                5L
                        )
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(5)
                )
                .andExpect(
                        jsonPath("$.beneficiaryAccountId")
                                .value(15)
                )
                .andExpect(
                        jsonPath("$.beneficiaryAccountNumber")
                                .value("555555555555")
                )
                .andExpect(
                        jsonPath("$.nickname")
                                .value("Friend")
                );

        verify(authenticatedUser)
                .getUserId(jwt);

        verify(beneficiaryService)
                .getBeneficiary(
                        42L,
                        5L
                );
    }

    @Test
    void getBeneficiary_shouldMapResourceNotFoundTo404()
            throws Exception {

        authenticateUser(42L);

        when(
                beneficiaryService.getBeneficiary(
                        42L,
                        99L
                )
        ).thenThrow(
                new ResourceNotFoundException(
                        "Beneficiary not found"
                )
        );

        mockMvc.perform(
                        get(
                                "/api/beneficiaries/{beneficiaryId}",
                                99L
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value("Beneficiary not found")
                );
    }

    @Test
    void deleteBeneficiary_shouldReturn204()
            throws Exception {

        Jwt jwt = authenticateUser(42L);

        doNothing().when(
                beneficiaryService
        ).deleteBeneficiary(
                42L,
                5L
        );

        mockMvc.perform(
                        delete(
                                "/api/beneficiaries/{beneficiaryId}",
                                5L
                        )
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(authenticatedUser)
                .getUserId(jwt);

        verify(beneficiaryService)
                .deleteBeneficiary(
                        42L,
                        5L
                );
    }

    @Test
    void deleteBeneficiary_shouldMapBusinessRuleExceptionTo400()
            throws Exception {

        authenticateUser(42L);

        doThrow(
                new BusinessRuleException(
                        "You do not have access to this beneficiary"
                )
        ).when(
                beneficiaryService
        ).deleteBeneficiary(
                42L,
                99L
        );

        mockMvc.perform(
                        delete(
                                "/api/beneficiaries/{beneficiaryId}",
                                99L
                        )
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.error")
                                .value("BUSINESS_RULE_VIOLATION")
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "You do not have access to this beneficiary"
                                )
                );
    }

    @Test
    void deleteBeneficiary_shouldMapResourceNotFoundTo404()
            throws Exception {

        authenticateUser(42L);

        doThrow(
                new ResourceNotFoundException(
                        "Beneficiary not found"
                )
        ).when(
                beneficiaryService
        ).deleteBeneficiary(
                42L,
                99L
        );

        mockMvc.perform(
                        delete(
                                "/api/beneficiaries/{beneficiaryId}",
                                99L
                        )
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value("Beneficiary not found")
                );
    }

    @Test
    void createBeneficiary_shouldReturn400WhenAccountNumberExceeds20Characters()
            throws Exception {

        authenticateUser(42L);

        String requestBody = """
                {
                    "beneficiaryAccountNumber": "123456789012345678901",
                    "nickname": "Test"
                }
                """;

        mockMvc.perform(
                        post("/api/beneficiaries")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(beneficiaryService);
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