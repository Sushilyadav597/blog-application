package com.blog.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JWT Token Provider Tests")
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private final String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private final long expirationMs = 3600000; // 1 hour

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(secret, expirationMs);
    }

    @Test
    @DisplayName("Should generate valid JWT token from user details")
    void shouldGenerateValidToken() {
        String token = jwtTokenProvider.generateTokenFromUser(1L, "sushil@example.com", "ROLE_USER");

        assertNotNull(token);
        assertFalse(token.isBlank());
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("Should extract correct user ID from valid JWT token")
    void shouldExtractUserIdFromJwt() {
        Long expectedUserId = 42L;
        String token = jwtTokenProvider.generateTokenFromUser(expectedUserId, "dev@example.com", "ROLE_ADMIN");

        Long actualUserId = jwtTokenProvider.getUserIdFromJWT(token);

        assertEquals(expectedUserId, actualUserId);
    }

    @Test
    @DisplayName("Should fail validation for invalid or tampered JWT token")
    void shouldFailValidationForInvalidToken() {
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalidPayload.signature";

        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }

    @Test
    @DisplayName("Should fail validation for expired JWT token")
    void shouldFailValidationForExpiredToken() {
        // Create a provider with negative expiration to instantly expire tokens
        JwtTokenProvider expiredProvider = new JwtTokenProvider(secret, -1000);
        String token = expiredProvider.generateTokenFromUser(1L, "expired@example.com", "ROLE_USER");

        assertFalse(jwtTokenProvider.validateToken(token));
    }
}
