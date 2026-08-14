package com.digitalbanking.auth.dto;

import java.util.Set;

public class RegisterResponse {

    private Long id;
    private String username;
    private String email;
    private Set<String> roles;

    public RegisterResponse() {
    }

    public RegisterResponse(Long id, String username, String email, Set<String> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return roles;
    }
}