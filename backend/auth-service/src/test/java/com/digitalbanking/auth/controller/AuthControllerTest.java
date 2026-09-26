package com.digitalbanking.auth.controller;

import com.digitalbanking.auth.dto.LoginRequest;
import com.digitalbanking.auth.dto.LoginResponse;
import com.digitalbanking.auth.dto.RefreshTokenResponse;
import com.digitalbanking.auth.dto.RegisterRequest;
import com.digitalbanking.auth.dto.RegisterResponse;
import com.digitalbanking.auth.exception.GlobalExceptionHandler;
import com.digitalbanking.auth.exception.UserAlreadyExistsException;
import com.digitalbanking.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void register_shouldReturn201AndResponseBody() throws Exception {
        RegisterResponse response = new RegisterResponse(
                1L,
                "testuser",
                "test@example.com",
                Set.of("ROLE_USER")
        );

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(response);

        String requestBody = """
                {
                    "username": "testuser",
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void register_shouldReturn400ForInvalidRequest() throws Exception {
        String requestBody = """
                {
                    "username": "",
                    "email": "invalid-email",
                    "password": ""
                }
                """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));

        verifyNoInteractions(authService);
    }

    @Test
    void register_shouldReturn409WhenUserAlreadyExists() throws Exception {
        when(authService.register(any(RegisterRequest.class)))
                .thenThrow(
                        new UserAlreadyExistsException(
                                "Username is already registered"
                        )
                );

        String requestBody = """
                {
                    "username": "testuser",
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("USER_ALREADY_EXISTS"))
                .andExpect(jsonPath("$.message")
                        .value("Username is already registered"));

        verify(authService).register(any(RegisterRequest.class));
    }

    @Test
    void login_shouldReturn200AndTokenResponse() throws Exception {
        LoginResponse response = new LoginResponse(
                "access-token",
                "refresh-token",
                "Bearer",
                3600L,
                "testuser",
                Set.of("ROLE_USER")
        );

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        String requestBody = """
                {
                    "username": "testuser",
                    "password": "password123"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));

        verify(authService).login(any(LoginRequest.class));
    }

    @Test
    void login_shouldReturn400ForInvalidRequest() throws Exception {
        String requestBody = """
                {
                    "username": "",
                    "password": ""
                }
                """;

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));

        verifyNoInteractions(authService);
    }

    @Test
    void refresh_shouldReturn200AndNewAccessToken() throws Exception {
        RefreshTokenResponse response = new RefreshTokenResponse(
                "new-access-token",
                "Bearer",
                3600L,
                "testuser",
                Set.of("ROLE_USER")
        );

        when(authService.refreshAccessToken("refresh-token"))
                .thenReturn(response);

        String requestBody = """
                {
                    "refreshToken": "refresh-token"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("new-access-token"))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(3600))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));

        verify(authService)
                .refreshAccessToken("refresh-token");
    }

    @Test
    void refresh_shouldReturn400WhenTokenIsBlank() throws Exception {
        String requestBody = """
                {
                    "refreshToken": ""
                }
                """;

        mockMvc.perform(
                        post("/api/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));

        verifyNoInteractions(authService);
    }

    @Test
    void logout_shouldReturn204() throws Exception {
        doNothing()
                .when(authService)
                .logout("refresh-token");

        String requestBody = """
                {
                    "refreshToken": "refresh-token"
                }
                """;

        mockMvc.perform(
                        post("/api/auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(authService)
                .logout("refresh-token");
    }

    @Test
    void logout_shouldReturn400WhenTokenIsBlank() throws Exception {
        String requestBody = """
                {
                    "refreshToken": ""
                }
                """;

        mockMvc.perform(
                        post("/api/auth/logout")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));

        verifyNoInteractions(authService);
    }
}