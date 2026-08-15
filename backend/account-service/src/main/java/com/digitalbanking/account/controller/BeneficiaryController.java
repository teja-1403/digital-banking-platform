package com.digitalbanking.account.controller;

import com.digitalbanking.account.dto.BeneficiaryResponse;
import com.digitalbanking.account.dto.CreateBeneficiaryRequest;
import com.digitalbanking.account.security.AuthenticatedUser;
import com.digitalbanking.account.service.BeneficiaryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiaries")
@SecurityRequirement(name = "BearerAuth")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;
    private final AuthenticatedUser authenticatedUser;

    public BeneficiaryController(
            BeneficiaryService beneficiaryService,
            AuthenticatedUser authenticatedUser
    ) {
        this.beneficiaryService = beneficiaryService;
        this.authenticatedUser = authenticatedUser;
    }

    @PostMapping
    public ResponseEntity<BeneficiaryResponse> createBeneficiary(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateBeneficiaryRequest request
    ) {

        Long userId = authenticatedUser.getUserId(jwt);

        BeneficiaryResponse response =
                beneficiaryService.createBeneficiary(
                        userId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<BeneficiaryResponse>>
    getCurrentUserBeneficiaries(
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = authenticatedUser.getUserId(jwt);

        return ResponseEntity.ok(
                beneficiaryService
                        .getCurrentUserBeneficiaries(userId)
        );
    }

    @GetMapping("/{beneficiaryId}")
    public ResponseEntity<BeneficiaryResponse> getBeneficiary(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long beneficiaryId
    ) {

        Long userId = authenticatedUser.getUserId(jwt);

        return ResponseEntity.ok(
                beneficiaryService.getBeneficiary(
                        userId,
                        beneficiaryId
                )
        );
    }

    @DeleteMapping("/{beneficiaryId}")
    public ResponseEntity<Void> deleteBeneficiary(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long beneficiaryId
    ) {

        Long userId = authenticatedUser.getUserId(jwt);

        beneficiaryService.deleteBeneficiary(
                userId,
                beneficiaryId
        );

        return ResponseEntity.noContent().build();
    }
}