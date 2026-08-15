package com.digitalbanking.account.controller;

import com.digitalbanking.account.dto.CreateCustomerRequest;
import com.digitalbanking.account.dto.CustomerResponse;
import com.digitalbanking.account.security.AuthenticatedUser;
import com.digitalbanking.account.service.CustomerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@SecurityRequirement(name = "BearerAuth")
public class CustomerController {

    private final CustomerService customerService;
    private final AuthenticatedUser authenticatedUser;

    public CustomerController(
            CustomerService customerService,
            AuthenticatedUser authenticatedUser
    ) {
        this.customerService = customerService;
        this.authenticatedUser = authenticatedUser;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateCustomerRequest request
    ) {

        Long userId = authenticatedUser.getUserId(jwt);

        CustomerResponse response =
                customerService.createCustomer(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<CustomerResponse> getCurrentCustomer(
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId = authenticatedUser.getUserId(jwt);

        return ResponseEntity.ok(
                customerService.getCurrentCustomer(userId)
        );
    }
}