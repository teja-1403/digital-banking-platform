package com.digitalbanking.auth.security;

import com.digitalbanking.auth.entity.Role;
import com.digitalbanking.auth.entity.User;
import com.digitalbanking.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        customUserDetailsService =
                new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetailsWithAuthorities() {
        Role userRole = new Role("ROLE_USER");
        Role adminRole = new Role("ROLE_ADMIN");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encoded-password");
        user.setEmail("test@example.com");
        user.setEnabled(true);
        user.setRoles(Set.of(userRole, adminRole));

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);

        assertEquals(
                "testuser",
                userDetails.getUsername()
        );

        assertEquals(
                "encoded-password",
                userDetails.getPassword()
        );

        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());

        Set<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertEquals(
                Set.of("ROLE_USER", "ROLE_ADMIN"),
                authorities
        );

        verify(userRepository)
                .findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_shouldMarkDisabledUserAsDisabled() {
        User user = new User();
        user.setId(2L);
        user.setUsername("disableduser");
        user.setPassword("encoded-password");
        user.setEmail("disabled@example.com");
        user.setEnabled(false);
        user.setRoles(
                Set.of(new Role("ROLE_USER"))
        );

        when(userRepository.findByUsername("disableduser"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails =
                customUserDetailsService.loadUserByUsername(
                        "disableduser"
                );

        assertNotNull(userDetails);

        assertEquals(
                "disableduser",
                userDetails.getUsername()
        );

        assertFalse(userDetails.isEnabled());

        Set<String> authorities = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        assertEquals(
                Set.of("ROLE_USER"),
                authorities
        );

        verify(userRepository)
                .findByUsername("disableduser");
    }

    @Test
    void loadUserByUsername_shouldThrowWhenUserDoesNotExist() {
        when(userRepository.findByUsername("missinguser"))
                .thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(
                        "missinguser"
                )
        );

        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(userRepository)
                .findByUsername("missinguser");
    }
}