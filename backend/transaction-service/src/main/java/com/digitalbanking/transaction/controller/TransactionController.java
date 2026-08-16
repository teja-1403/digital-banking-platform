package com.digitalbanking.transaction.controller;

import com.digitalbanking.transaction.dto.TransactionHistoryResponse;
import com.digitalbanking.transaction.dto.TransactionResponse;
import com.digitalbanking.transaction.dto.TransferRequest;
import com.digitalbanking.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@SecurityRequirement(name = "BearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService
    ) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transfers")
    public ResponseEntity<TransactionResponse> transfer(
            @AuthenticationPrincipal Jwt jwt,
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody TransferRequest request
    ) {

        Long userId = jwt.getClaim("userId");

        TransactionResponse response =
                transactionService.initiateTransfer(
                        userId,
                        idempotencyKey,
                        request
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionHistoryResponse>>
    getAccountHistory(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long accountId
    ) {

        Long userId = jwt.getClaim("userId");

        return ResponseEntity.ok(
                transactionService.getAccountHistory(
                        userId,
                        accountId
                )
        );
    }
}