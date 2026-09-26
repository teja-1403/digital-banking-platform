package com.digitalbanking.auth.service;

import com.digitalbanking.auth.dto.LoginRequest;
import com.digitalbanking.auth.dto.LoginResponse;
import com.digitalbanking.auth.dto.RefreshTokenResponse;
import com.digitalbanking.auth.dto.RegisterRequest;
import com.digitalbanking.auth.dto.RegisterResponse;
import com.digitalbanking.auth.entity.RefreshToken;
import com.digitalbanking.auth.entity.Role;
import com.digitalbanking.auth.entity.User;
import com.digitalbanking.auth.exception.RoleNotFoundException;
import com.digitalbanking.auth.exception.UserAlreadyExistsException;
import com.digitalbanking.auth.repository.RoleRepository;
import com.digitalbanking.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtEncoder jwtEncoder;

    @InjectMocks
    private AuthService authService;

    private Role userRole;
    private User user;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                authService,
                "jwtExpiration",
                3_600_000L
        );

        userRole = new Role("ROLE_USER");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encoded-password");
        user.setEnabled(true);
        user.getRoles().add(userRole);
    }

    @Test
    void register_shouldCreateUserSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("testuser"))
                .thenReturn(false);

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);

        when(roleRepository.findByName("ROLE_USER"))
                .thenReturn(Optional.of(userRole));

        when(passwordEncoder.encode("password123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User savedUser = invocation.getArgument(0);
                    savedUser.setId(1L);
                    return savedUser;
                });

        RegisterResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("testuser", response.getUsername());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(Set.of("ROLE_USER"), response.getRoles());

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("testuser", savedUser.getUsername());
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPassword());
        assertTrue(savedUser.isEnabled());
        assertEquals(Set.of(userRole), savedUser.getRoles());

        verify(passwordEncoder).encode("password123");
        verify(roleRepository).findByName("ROLE_USER");
    }

    @Test
    void register_shouldRejectDuplicateUsername() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("testuser"))
                .thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(request)
        );

        assertEquals(
                "Username is already registered",
                exception.getMessage()
        );

        verify(userRepository)
                .existsByUsername("testuser");

        verify(userRepository, never())
                .existsByEmail(anyString());

        verifyNoInteractions(
                roleRepository,
                passwordEncoder
        );
    }

    @Test
    void register_shouldRejectDuplicateEmail() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("testuser"))
                .thenReturn(false);

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> authService.register(request)
        );

        assertEquals(
                "Email is already registered",
                exception.getMessage()
        );

        verify(userRepository)
                .existsByUsername("testuser");

        verify(userRepository)
                .existsByEmail("test@example.com");

        verifyNoInteractions(
                roleRepository,
                passwordEncoder
        );
    }

    @Test
    void register_shouldFailWhenDefaultUserRoleDoesNotExist() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userRepository.existsByUsername("testuser"))
                .thenReturn(false);

        when(userRepository.existsByEmail("test@example.com"))
                .thenReturn(false);

        when(roleRepository.findByName("ROLE_USER"))
                .thenReturn(Optional.empty());

        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> authService.register(request)
        );

        assertEquals(
                "Default user role not found",
                exception.getMessage()
        );

        verify(roleRepository)
                .findByName("ROLE_USER");

        verify(userRepository, never())
                .save(any(User.class));

        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void login_shouldAuthenticateAndReturnTokens() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        Jwt jwt = Jwt.withTokenValue("access-token")
                .header("alg", "HS256")
                .claim("sub", "testuser")
                .build();

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(
                new UsernamePasswordAuthenticationToken(
                        "testuser",
                        "password123"
                )
        );

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(jwtEncoder.encode(
                any(JwtEncoderParameters.class)
        )).thenReturn(jwt);

        when(refreshTokenService.createRefreshToken(user))
                .thenReturn(refreshToken);

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
        assertEquals("testuser", response.getUsername());
        assertEquals(Set.of("ROLE_USER"), response.getRoles());

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );

        verify(userRepository)
                .findByUsername("testuser");

        verify(jwtEncoder)
                .encode(any(JwtEncoderParameters.class));

        verify(refreshTokenService)
                .createRefreshToken(user);
    }

    @Test
    void login_shouldFailWhenUserIsNotFoundAfterAuthentication() {
        LoginRequest request = new LoginRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(
                new UsernamePasswordAuthenticationToken(
                        "testuser",
                        "password123"
                )
        );

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> authService.login(request)
        );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(authenticationManager).authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        );

        verify(userRepository)
                .findByUsername("testuser");

        verifyNoInteractions(
                jwtEncoder,
                refreshTokenService
        );
    }

    @Test
    void refreshAccessToken_shouldReturnNewAccessToken() {
        String refreshTokenValue = "refresh-token";

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(refreshTokenValue);
        refreshToken.setUser(user);
        refreshToken.setRevoked(false);

        Jwt jwt = Jwt.withTokenValue("new-access-token")
                .header("alg", "HS256")
                .claim("sub", "testuser")
                .build();

        when(refreshTokenService.validateRefreshToken(refreshTokenValue))
                .thenReturn(refreshToken);

        when(jwtEncoder.encode(
                any(JwtEncoderParameters.class)
        )).thenReturn(jwt);

        RefreshTokenResponse response =
                authService.refreshAccessToken(refreshTokenValue);

        assertNotNull(response);
        assertEquals(
                "new-access-token",
                response.getAccessToken()
        );
        assertEquals("Bearer", response.getTokenType());
        assertEquals(3600L, response.getExpiresIn());
        assertEquals("testuser", response.getUsername());
        assertEquals(Set.of("ROLE_USER"), response.getRoles());

        verify(refreshTokenService)
                .validateRefreshToken(refreshTokenValue);

        verify(jwtEncoder)
                .encode(any(JwtEncoderParameters.class));
    }

    @Test
    void logout_shouldRevokeRefreshToken() {
        String refreshToken = "refresh-token";

        authService.logout(refreshToken);

        verify(refreshTokenService)
                .revokeToken(refreshToken);
    }
}