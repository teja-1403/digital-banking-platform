package com.digitalbanking.account.controller;

import com.digitalbanking.account.dto.AccountOwnershipResponse;
import com.digitalbanking.account.dto.InternalTransferRequest;
import com.digitalbanking.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/accounts")
public class InternalAccountController {

    private final AccountService accountService;

    public InternalAccountController(
            AccountService accountService
    ) {
        this.accountService = accountService;
    }

    @PostMapping("/transfer")
    public ResponseEntity<Void> executeTransfer(
            @Valid @RequestBody InternalTransferRequest request
    ) {

        accountService.executeInternalTransfer(
                request.getUserId(),
                request.getSourceAccountId(),
                request.getDestinationAccountId(),
                request.getAmount()
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{accountId}/ownership")
    public ResponseEntity<AccountOwnershipResponse> checkOwnership(
            @RequestParam Long userId,
            @PathVariable Long accountId
    ) {

        boolean owner =
                accountService.isAccountOwnedByUser(
                        userId,
                        accountId
                );

        return ResponseEntity.ok(
                new AccountOwnershipResponse(
                        accountId,
                        owner
                )
        );
    }
}