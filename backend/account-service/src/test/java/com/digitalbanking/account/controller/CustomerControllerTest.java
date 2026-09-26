package com.digitalbanking.account.controller;

import com.digitalbanking.account.dto.CustomerResponse;
import com.digitalbanking.account.exception.BusinessRuleException;
import com.digitalbanking.account.exception.GlobalExceptionHandler;
import com.digitalbanking.account.exception.ResourceNotFoundException;
import com.digitalbanking.account.security.AuthenticatedUser;
import com.digitalbanking.account.service.CustomerService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({
        GlobalExceptionHandler.class,
        CustomerControllerTest.SecurityTestConfig.class
})
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

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

    @Test
    void createCustomer_shouldReturn201()
            throws Exception {

        Jwt jwt = authenticateUser(42L);

        CustomerResponse response =
                new CustomerResponse(
                        1L,
                        42L,
                        "Sai",
                        "Teja",
                        "9876543210"
                );

        when(
                customerService.createCustomer(
                        eq(42L),
                        any()
                )
        ).thenReturn(response);

        String requestBody = """
                {
                    "firstName": "Sai",
                    "lastName": "Teja",
                    "phoneNumber": "9876543210"
                }
                """;

        mockMvc.perform(
                        post("/api/customers")
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
                        jsonPath("$.userId")
                                .value(42)
                )
                .andExpect(
                        jsonPath("$.firstName")
                                .value("Sai")
                )
                .andExpect(
                        jsonPath("$.lastName")
                                .value("Teja")
                )
                .andExpect(
                        jsonPath("$.phoneNumber")
                                .value("9876543210")
                );

        verify(authenticatedUser)
                .getUserId(jwt);

        verify(customerService)
                .createCustomer(
                        eq(42L),
                        any()
                );
    }

    @Test
    void createCustomer_shouldReturn400ForInvalidRequest()
            throws Exception {

        authenticateUser(42L);

        String requestBody = """
                {
                    "firstName": "",
                    "lastName": "",
                    "phoneNumber": "9876543210"
                }
                """;

        mockMvc.perform(
                        post("/api/customers")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());

        verifyNoInteractions(customerService);
    }

    @Test
    void createCustomer_shouldMapBusinessRuleExceptionTo400()
            throws Exception {

        authenticateUser(42L);

        when(
                customerService.createCustomer(
                        eq(42L),
                        any()
                )
        ).thenThrow(
                new BusinessRuleException(
                        "Customer profile already exists for this user"
                )
        );

        String requestBody = """
                {
                    "firstName": "Sai",
                    "lastName": "Teja",
                    "phoneNumber": "9876543210"
                }
                """;

        mockMvc.perform(
                        post("/api/customers")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Customer profile already exists for this user"
                                )
                );
    }

    @Test
    void getCurrentCustomer_shouldReturn200()
            throws Exception {

        Jwt jwt = authenticateUser(42L);

        CustomerResponse response =
                new CustomerResponse(
                        1L,
                        42L,
                        "Sai",
                        "Teja",
                        "9876543210"
                );

        when(
                customerService.getCurrentCustomer(42L)
        ).thenReturn(response);

        mockMvc.perform(
                        get("/api/customers/me")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.userId")
                                .value(42)
                )
                .andExpect(
                        jsonPath("$.firstName")
                                .value("Sai")
                )
                .andExpect(
                        jsonPath("$.lastName")
                                .value("Teja")
                )
                .andExpect(
                        jsonPath("$.phoneNumber")
                                .value("9876543210")
                );

        verify(authenticatedUser)
                .getUserId(jwt);

        verify(customerService)
                .getCurrentCustomer(42L);
    }

    @Test
    void getCurrentCustomer_shouldMapResourceNotFoundTo404()
            throws Exception {

        authenticateUser(42L);

        when(
                customerService.getCurrentCustomer(42L)
        ).thenThrow(
                new ResourceNotFoundException(
                        "Customer profile not found"
                )
        );

        mockMvc.perform(
                        get("/api/customers/me")
                )
                .andExpect(status().isNotFound())
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Customer profile not found"
                                )
                );
    }

    @Test
    void getCurrentCustomer_shouldReturnCustomerWithNullPhoneNumber()
            throws Exception {

        Jwt jwt = authenticateUser(42L);

        CustomerResponse response =
                new CustomerResponse(
                        1L,
                        42L,
                        "Sai",
                        "Teja",
                        null
                );

        when(
                customerService.getCurrentCustomer(42L)
        ).thenReturn(response);

        mockMvc.perform(
                        get("/api/customers/me")
                )
                .andExpect(status().isOk())
                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.userId")
                                .value(42)
                )
                .andExpect(
                        jsonPath("$.firstName")
                                .value("Sai")
                )
                .andExpect(
                        jsonPath("$.lastName")
                                .value("Teja")
                )
                .andExpect(
                        jsonPath("$.phoneNumber")
                                .doesNotExist()
                );

        verify(authenticatedUser)
                .getUserId(jwt);

        verify(customerService)
                .getCurrentCustomer(42L);
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