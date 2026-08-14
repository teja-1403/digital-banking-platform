package com.digitalbanking.auth.controller;

import com.digitalbanking.auth.dto.LoginRequest;
import com.digitalbanking.auth.dto.LoginResponse;
import com.digitalbanking.auth.dto.RegisterRequest;
import com.digitalbanking.auth.dto.RegisterResponse;
import com.digitalbanking.auth.service.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.digitalbanking.auth.dto.UserInfoResponse;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import java.util.Set;
import com.digitalbanking.auth.dto.RefreshTokenRequest;
import com.digitalbanking.auth.dto.RefreshTokenResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        RegisterResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.login(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "BearerAuth")
    public ResponseEntity<UserInfoResponse> getCurrentUser(
            @AuthenticationPrincipal Jwt jwt
    ) {
        Long userId = jwt.getClaim("userId");
        String username = jwt.getSubject();

        Set<String> roles = Set.copyOf(
                jwt.getClaimAsStringList("roles")
        );

        return ResponseEntity.ok(
                new UserInfoResponse(
                        userId,
                        username,
                        roles
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(
                authService.refreshAccessToken(request.getRefreshToken())
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        authService.logout(request.getRefreshToken());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/admin-test")
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> adminTest() {
        return ResponseEntity.ok("Admin authorization successful");
    }
}