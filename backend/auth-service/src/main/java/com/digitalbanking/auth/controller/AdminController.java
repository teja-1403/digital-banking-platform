package com.digitalbanking.auth.controller;

import com.digitalbanking.auth.dto.AdminUserStatsResponse;
import com.digitalbanking.auth.repository.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/user-stats")
    public AdminUserStatsResponse getUserStats() {

        return new AdminUserStatsResponse(
                userRepository.count()
        );
    }
}