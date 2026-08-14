package com.digitalbanking.auth.service;

import com.digitalbanking.auth.dto.LoginRequest;
import com.digitalbanking.auth.dto.LoginResponse;
import com.digitalbanking.auth.dto.RegisterRequest;
import com.digitalbanking.auth.dto.RegisterResponse;
import com.digitalbanking.auth.entity.Role;
import com.digitalbanking.auth.entity.User;
import com.digitalbanking.auth.exception.RoleNotFoundException;
import com.digitalbanking.auth.exception.UserAlreadyExistsException;
import com.digitalbanking.auth.repository.RoleRepository;
import com.digitalbanking.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.digitalbanking.auth.dto.RefreshTokenResponse;
import com.digitalbanking.auth.entity.RefreshToken;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtEncoder jwtEncoder,
            RefreshTokenService refreshTokenService
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username is already registered");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email is already registered");
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() ->
                        new RoleNotFoundException("Default user role not found")
                );

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEnabled(true);
        user.getRoles().add(userRole);

        User savedUser = userRepository.save(user);

        Set<String> roles = savedUser.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                roles
        );
    }

    public LoginResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );

        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("digital-banking-platform")
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plusMillis(jwtExpiration))
                .claim("userId", user.getId())
                .claim("roles", roles)
                .build();

        String accessToken = jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();

        RefreshToken refreshToken =
                refreshTokenService.createRefreshToken(user);

        return new LoginResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                jwtExpiration / 1000,
                user.getUsername(),
                roles
        );
    }

    public RefreshTokenResponse refreshAccessToken(String refreshTokenValue) {

        RefreshToken refreshToken =
                refreshTokenService.validateRefreshToken(refreshTokenValue);

        User user = refreshToken.getUser();

        Set<String> roles = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("digital-banking-platform")
                .subject(user.getUsername())
                .issuedAt(now)
                .expiresAt(now.plusMillis(jwtExpiration))
                .claim("userId", user.getId())
                .claim("roles", roles)
                .build();

        String accessToken = jwtEncoder
                .encode(JwtEncoderParameters.from(claims))
                .getTokenValue();

        return new RefreshTokenResponse(
                accessToken,
                "Bearer",
                jwtExpiration / 1000,
                user.getUsername(),
                roles
        );
    }

    public void logout(String refreshToken) {
        refreshTokenService.revokeToken(refreshToken);
    }
}