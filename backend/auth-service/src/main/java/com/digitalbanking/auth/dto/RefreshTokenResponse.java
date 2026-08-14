package com.digitalbanking.auth.dto;

import java.util.Set;

public class RefreshTokenResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private String username;
    private Set<String> roles;

    public RefreshTokenResponse() {
    }

    public RefreshTokenResponse(
            String accessToken,
            String tokenType,
            long expiresIn,
            String username,
            Set<String> roles
    ) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.username = username;
        this.roles = roles;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getRoles() {
        return roles;
    }
}