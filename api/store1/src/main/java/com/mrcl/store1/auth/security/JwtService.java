package com.mrcl.store1.auth.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Creates and reads JWT tokens using jjwt.
 * Uses HMAC shared secret for local authentication.
 */
@Component
public class JwtService {

    private static final String ROLES_CLAIM = "roles";

    private final SecretKey signingKey;
    private final long expirationMinutes;

    public JwtService(@Value("${jwt.secret}") String secret,
                      @Value("${jwt.expiration-minutes}") long expirationMinutes) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    public String generateToken(String email, Set<String> roles) {
        Instant issuedAt = Instant.now();
        Instant expiration = calculateExpiration(issuedAt);

        return Jwts.builder()
                .subject(email)
                .claim(ROLES_CLAIM, normalizeRoles(roles))
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(signingKey)
                .compact();
    }

    public String extractSubject(String token) {
        return parseClaims(token).getSubject();
    }

    public Set<String> extractRoles(String token) {
        Claims claims = parseClaims(token);
        Object rolesClaim = claims.get(ROLES_CLAIM);

        if (!(rolesClaim instanceof List<?> rolesList)) {
            return Set.of();
        }

        Set<String> roles = new HashSet<>();

        for (Object item : rolesList) {
            if (item != null) {
                roles.add(item.toString());
            }
        }

        return roles;
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private Instant calculateExpiration(Instant issuedAt) {
        return issuedAt.plus(expirationMinutes, ChronoUnit.MINUTES);
    }

    private Set<String> normalizeRoles(Set<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }

        Set<String> normalizedRoles = new HashSet<>();

        for (String role : roles) {
            if (role != null && !role.isBlank()) {
                normalizedRoles.add(role.trim());
            }
        }

        return normalizedRoles;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}