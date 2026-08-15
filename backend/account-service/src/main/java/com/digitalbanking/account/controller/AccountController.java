package com.digitalbanking.account.controller;

import com.digitalbanking.account.dto.AccountResponse;
import com.digitalbanking.account.dto.CreateAccountRequest;
import com.digitalbanking.account.security.AuthenticatedUser;
import com.digitalbanking.account.service.AccountService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@SecurityRequirement(name = "BearerAuth")
public class AccountController {

    private final AccountService accountService;
    private final AuthenticatedUser authenticatedUser;

    public AccountController(
            AccountService accountService,
            AuthenticatedUser authenticatedUser
    ) {
        this.accountService = accountService;
        this.authenticatedUser = authenticatedUser;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateAccountRequest request
    ) {

        Long userId = authenticatedUser.getUserId(jwt);

        AccountResponse response =
                accountService.createAccount(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> getCurrentUserAccounts(
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = authenticatedUser.getUserId(jwt);

        return ResponseEntity.ok(
                accountService.getCurrentUserAccounts(userId)
        );
    }

    @GetMapping("/{accountId}")
    public ResponseEntity<AccountResponse> getAccount(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long accountId
    ) {

        Long userId = authenticatedUser.getUserId(jwt);

        return ResponseEntity.ok(
                accountService.getAccount(userId, accountId)
        );
    }
}