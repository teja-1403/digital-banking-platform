package com.digitalbanking.account.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class AuthenticatedUser {

    public Long getUserId(Jwt jwt) {
        return jwt.getClaim("userId");
    }

    public String getUsername(Jwt jwt) {
        return jwt.getSubject();
    }
}