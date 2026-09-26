package com.digitalbanking.auth.controller;

import com.digitalbanking.auth.config.SecurityConfig;
import com.digitalbanking.auth.exception.GlobalExceptionHandler;
import com.digitalbanking.auth.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({
        SecurityConfig.class,
        GlobalExceptionHandler.class
})
class AuthSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void me_shouldReturn401WhenUserIsNotAuthenticated() throws Exception {
        mockMvc.perform(
                        get("/api/auth/me")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_shouldReturnCurrentUserForAuthenticatedUser() throws Exception {
        mockMvc.perform(
                        get("/api/auth/me")
                                .with(jwt()
                                        .jwt(jwt -> jwt
                                                .subject("testuser")
                                                .claim("userId", 1L)
                                                .claim(
                                                        "roles",
                                                        List.of("ROLE_USER")
                                                )
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));
    }

    @Test
    void adminTest_shouldReturn403ForRegularUser() throws Exception {
        mockMvc.perform(
                        get("/api/auth/admin-test")
                                .with(jwt()
                                        .authorities(
                                                new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                                        "ROLE_USER"
                                                )
                                        )
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminTest_shouldReturn200ForAdmin() throws Exception {
        mockMvc.perform(
                        get("/api/auth/admin-test")
                                .with(jwt()
                                        .authorities(
                                                new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                                        "ROLE_ADMIN"
                                                )
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().string(
                                "Admin authorization successful"
                        )
                );
    }
}