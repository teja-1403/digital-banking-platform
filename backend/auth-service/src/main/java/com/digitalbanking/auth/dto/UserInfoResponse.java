package com.digitalbanking.auth.dto;

import java.util.Set;

public class UserInfoResponse {

    private Long userId;
    private String username;
    private Set<String> roles;

    public UserInfoResponse() {
    }

    public UserInfoResponse(Long userId, String username, Set<String> roles) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getRoles() {
        return roles;
    }
}