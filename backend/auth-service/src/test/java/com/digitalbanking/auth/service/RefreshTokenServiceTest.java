package com.digitalbanking.auth.service;

import com.digitalbanking.auth.entity.RefreshToken;
import com.digitalbanking.auth.entity.User;
import com.digitalbanking.auth.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenService refreshTokenService;

    private User user;

    @BeforeEach
    void setUp() {
        refreshTokenService =
                new RefreshTokenService(
                        refreshTokenRepository,
                        60_000L
                );

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
    }

    @Test
    void createRefreshToken_shouldDeleteExistingTokensAndSaveNewToken() {
        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        LocalDateTime before = LocalDateTime.now();

        RefreshToken result =
                refreshTokenService.createRefreshToken(user);

        LocalDateTime after = LocalDateTime.now();

        assertNotNull(result);
        assertNotNull(result.getToken());
        assertFalse(result.getToken().isBlank());

        assertEquals(user, result.getUser());
        assertFalse(result.isRevoked());

        assertNotNull(result.getExpiryDate());

        assertTrue(
                !result.getExpiryDate().isBefore(before)
        );

        assertTrue(
                !result.getExpiryDate()
                        .isAfter(after.plusSeconds(65))
        );

        verify(refreshTokenRepository)
                .deleteByUser(user);

        verify(refreshTokenRepository)
                .save(any(RefreshToken.class));
    }

    @Test
    void validateRefreshToken_shouldReturnValidToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("valid-token");
        refreshToken.setUser(user);
        refreshToken.setRevoked(false);
        refreshToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(10)
        );

        when(refreshTokenRepository.findByToken("valid-token"))
                .thenReturn(Optional.of(refreshToken));

        RefreshToken result =
                refreshTokenService.validateRefreshToken(
                        "valid-token"
                );

        assertSame(refreshToken, result);

        verify(refreshTokenRepository)
                .findByToken("valid-token");
    }

    @Test
    void validateRefreshToken_shouldRejectMissingToken() {
        when(refreshTokenRepository.findByToken("missing-token"))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> refreshTokenService.validateRefreshToken(
                        "missing-token"
                )
        );

        assertEquals(
                "Invalid refresh token",
                exception.getMessage()
        );

        verify(refreshTokenRepository)
                .findByToken("missing-token");
    }

    @Test
    void validateRefreshToken_shouldRejectRevokedToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("revoked-token");
        refreshToken.setUser(user);
        refreshToken.setRevoked(true);
        refreshToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(10)
        );

        when(refreshTokenRepository.findByToken("revoked-token"))
                .thenReturn(Optional.of(refreshToken));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> refreshTokenService.validateRefreshToken(
                        "revoked-token"
                )
        );

        assertEquals(
                "Refresh token has been revoked",
                exception.getMessage()
        );

        verify(refreshTokenRepository)
                .findByToken("revoked-token");
    }

    @Test
    void validateRefreshToken_shouldRejectExpiredToken() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("expired-token");
        refreshToken.setUser(user);
        refreshToken.setRevoked(false);
        refreshToken.setExpiryDate(
                LocalDateTime.now().minusMinutes(10)
        );

        when(refreshTokenRepository.findByToken("expired-token"))
                .thenReturn(Optional.of(refreshToken));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> refreshTokenService.validateRefreshToken(
                        "expired-token"
                )
        );

        assertEquals(
                "Refresh token has expired",
                exception.getMessage()
        );

        verify(refreshTokenRepository)
                .findByToken("expired-token");
    }

    @Test
    void revokeToken_shouldMarkTokenAsRevokedAndSaveIt() {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");
        refreshToken.setUser(user);
        refreshToken.setRevoked(false);
        refreshToken.setExpiryDate(
                LocalDateTime.now().plusMinutes(10)
        );

        when(refreshTokenRepository.findByToken("refresh-token"))
                .thenReturn(Optional.of(refreshToken));

        refreshTokenService.revokeToken("refresh-token");

        assertTrue(refreshToken.isRevoked());

        ArgumentCaptor<RefreshToken> captor =
                ArgumentCaptor.forClass(RefreshToken.class);

        verify(refreshTokenRepository)
                .save(captor.capture());

        assertSame(
                refreshToken,
                captor.getValue()
        );

        assertTrue(
                captor.getValue().isRevoked()
        );
    }

    @Test
    void revokeToken_shouldRejectMissingToken() {
        when(refreshTokenRepository.findByToken("missing-token"))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> refreshTokenService.revokeToken(
                        "missing-token"
                )
        );

        assertEquals(
                "Invalid refresh token",
                exception.getMessage()
        );

        verify(refreshTokenRepository)
                .findByToken("missing-token");

        verify(
                refreshTokenRepository,
                never()
        ).save(any(RefreshToken.class));
    }
}